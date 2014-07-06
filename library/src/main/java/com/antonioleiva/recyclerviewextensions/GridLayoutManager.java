package com.antonioleiva.recyclerviewextensions;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class GridLayoutManager extends BaseLayoutManager {

    private static final int DEFAULT_COLUMNS = 2;
    private int columns;

    private static final String TAG = GridLayoutManager.class.getSimpleName();

    public GridLayoutManager(Context context){
        this(context, DEFAULT_COLUMNS, VERTICAL);
    }

    public GridLayoutManager(Context context, int columns, int orientation) {
        super(context, orientation, false);
        this.columns = columns;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    @Override
    protected int fill(RecyclerView.Recycler recycler, BaseLayoutManager.RenderState renderState,
                       RecyclerView.State state, boolean stopOnFocusable) {
        int itemWidth;
        if (mOrientation == VERTICAL) {
            itemWidth = (getWidth() - getPaddingLeft() - getPaddingRight()) / columns;
        }else{
            itemWidth = (getHeight() - getPaddingTop() - getPaddingBottom()) / columns;
        }
        // max offset we should set is mFastScroll + available
        final int start = renderState.mAvailable;
        if (renderState.mScrollingOffset != RenderState.SCOLLING_OFFSET_NaN) {
            // TODO ugly bug fix. should not happen
            if (renderState.mAvailable < 0) {
                renderState.mScrollingOffset += renderState.mAvailable;
            }
            recycleByRenderState(recycler, renderState);
        }
        int remainingSpace = renderState.mAvailable + renderState.mExtra;
        int columnCount = 0;
        while (remainingSpace > 0 && renderState.hasMore(state)) {
            View view = renderState.next(recycler);
            if (view == null) {
                if (DEBUG && renderState.mScrapList == null) {
                    throw new RuntimeException("received null view when unexpected");
                }
                // if we are laying out views in scrap, this may return null which means there is
                // no more items to layout.
                break;
            }
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
            if (!params.isItemRemoved() && mRenderState.mScrapList == null) {
                if (mShouldReverseLayout == (renderState.mLayoutDirection
                        == RenderState.LAYOUT_START)) {
                    addView(view);
                } else {
                    addView(view, 0);
                }
            }

            if (mOrientation == VERTICAL) {
                params.width = itemWidth - params.leftMargin - params.rightMargin;
            }else{
                params.height = itemWidth - params.topMargin - params.bottomMargin;
            }
            measureChildWithMargins(view, 0, 0);
            int consumed = mOrientationHelper.getDecoratedMeasurement(view);
            int left, top, right, bottom;
            if (mOrientation == VERTICAL) {
                if (isLayoutRTL() == (renderState.mLayoutDirection
                        == RenderState.LAYOUT_END)) {
                    right = getWidth() - getPaddingRight() - itemWidth * columnCount;
                    left = right - mOrientationHelper.getDecoratedMeasurementInOther(view);
                } else {
                    left = columnCount * itemWidth + getPaddingLeft();
                    right = left + mOrientationHelper.getDecoratedMeasurementInOther(view);
                }
                if (renderState.mLayoutDirection == RenderState.LAYOUT_START) {
                    bottom = renderState.mOffset;
                    top = renderState.mOffset - consumed;
                } else {
                    top = renderState.mOffset;
                    bottom = renderState.mOffset + consumed;
                }
            } else {
                if (renderState.mLayoutDirection == RenderState.LAYOUT_START) {
                    bottom = getHeight() - getPaddingBottom() - itemWidth * columnCount;
                    top = bottom - mOrientationHelper.getDecoratedMeasurementInOther(view);
                    right = renderState.mOffset;
                    left = renderState.mOffset - consumed;
                } else {
                    top = columnCount * itemWidth + getPaddingTop();
                    bottom = top + mOrientationHelper.getDecoratedMeasurementInOther(view);
                    left = renderState.mOffset;
                    right = renderState.mOffset + consumed;
                }
            }
            // We calculate everything with View's bounding box (which includes decor and margins)
            // To calculate correct layout position, we subtract margins.
            layoutDecorated(view, left + params.leftMargin, top + params.topMargin
                    , right - params.rightMargin, bottom - params.bottomMargin);
            if (DEBUG) {
                Log.d(TAG, "laid out child at position " + getPosition(view) + ", with l:"
                        + (left + params.leftMargin) + ", t:" + (top + params.topMargin) + ", r:"
                        + (right - params.rightMargin) + ", b:" + (bottom - params.bottomMargin));
            }

            if (!params.isItemRemoved()) {
                columnCount++;
                if (columnCount == columns) {
                    columnCount = 0;
                    renderState.mOffset += consumed * renderState.mLayoutDirection;
                    renderState.mAvailable -= consumed;
                    // we keep a separate remaining space because mAvailable is important for recycling
                    remainingSpace -= consumed;

                    if (renderState.mScrollingOffset != RenderState.SCOLLING_OFFSET_NaN) {
                        renderState.mScrollingOffset += consumed;
                        if (renderState.mAvailable < 0) {
                            renderState.mScrollingOffset += renderState.mAvailable;
                        }
                        recycleByRenderState(recycler, renderState);
                    }
                }
            }

            if (stopOnFocusable && view.isFocusable()) {
                break;
            }

            if (state != null && state.getTargetScrollPosition() == getPosition(view)) {
                break;
            }
        }
        if (DEBUG) {
            validateChildOrder();
        }
        return start - renderState.mAvailable;
    }
}
