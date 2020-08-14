package com.cyf.mn52.suit.util;

public class UtilPageOfJava {
    //总记录数
    public int total = 0;
    //每页显示记录数
    public int limit = 10;
    //总页数
    public int pages = 1;
    //当前页
    public int pageNumber = 1;
    //首页
    public int firstPage = 1;
    //尾页
    public int lastPage = 0;
    //上一页
    public int prePage = 0;
    //下一页
    public int nextPage = 0;
    //是否为第一页
    public boolean isFirstPage = false;
    //是否为最后一页
    public boolean isLastPage = false;
    //是否有前一页
    public boolean hasPreviousPage = false;
    //是否有下一页
    public boolean hasNextPage = false;
    //导航页码数
    public int navigatePages = 10;
    //所有导航页号
    public int[] navigatePageNumbers;

    public UtilPageOfJava(int pageNumber, int limit, int total, int navigatePages) {
        //设置基本参数
        this.total = total;
        this.limit = limit;
        this.pages = (this.total - 1) / this.limit + 1;
        this.firstPage = 1;
        this.lastPage = pages;
        this.prePage = pageNumber - 1;
        this.nextPage = pageNumber + 1;
        this.navigatePages = navigatePages;
        //根据输入可能错误的当前号码进行自动纠正
        if (pageNumber < 1) {
            this.pageNumber = 1;
        } else if (pageNumber > this.pages) {
            this.pageNumber = this.pages;
        } else {
            this.pageNumber = pageNumber;
        }
        //基本参数设定之后进行导航页面的计算
        calcNavigatePageNumbers();
        //以及页面边界的判定
        judgePageBoudary();
    }

    //计算导航页
    private void calcNavigatePageNumbers() {
        //当总页数小于或等于导航页码数时
        if (pages <= navigatePages) {
            navigatePageNumbers = new int[pages];
            for (int i = 0; i < pages; i++) {
                navigatePageNumbers[i] = i + 1;
            }
        } else { //当总页数大于导航页码数时
            navigatePageNumbers = new int[navigatePages];
            int startNum = pageNumber - navigatePages / 2;
            int endNum = pageNumber + navigatePages / 2;

            if (startNum < 1) {
                startNum = 1;
                //从第一页开始往后
                for (int i = 0; i < navigatePages; i++) {
                    navigatePageNumbers[i] = startNum++;
                }
            } else if (endNum > pages) {
                endNum = pages;
                //从最后一页开始往前
                for (int i = navigatePages - 1; i >= 0; i--) {
                    navigatePageNumbers[i] = endNum--;
                }
            } else {
                //所有中间页
                for (int i = 0; i < navigatePages; i++) {
                    navigatePageNumbers[i] = startNum++;
                }
            }
        }
    }

    //判定页面边界
    private void judgePageBoudary() {
        isFirstPage = pageNumber == 1;
        isLastPage = pageNumber == pages && pageNumber != 1;
        hasPreviousPage = pageNumber != 1;
        hasNextPage = pageNumber != pages;
    }

}
