package simple.chatgpt.util;

import java.util.List;

public class PagedResult<T> {

    private List<T> items;
    private long totalCount;
    private int page;
    private int size;
    private int maxPage; // 👈 new field

    public PagedResult(List<T> items, long totalCount, int page, int size) {
        this.items = items;
        this.totalCount = totalCount;
        this.page = page;
        this.size = size;
        this.maxPage = (int) Math.ceil((double) totalCount / size); // 👈 calculate here
    }

    // ---------------------
    // Getters and Setters
    // ---------------------
    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
        this.maxPage = (int) Math.ceil((double) totalCount / this.size); // keep in sync
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
        this.maxPage = (int) Math.ceil((double) this.totalCount / size); // keep in sync
    }

    public int getMaxPage() {
        return maxPage;
    }
}
