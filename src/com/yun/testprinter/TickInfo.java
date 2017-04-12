package com.yun.testprinter;


public class TickInfo {

    private String area;
    private long orderId;
    private long orderTicketId;
    private long price;
    private long showStartTime;
    private String productName;

    private String remark;
    private String seat;
    private String ticketNo;
    private String venueName;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getOrderTicketId() {
        return orderTicketId;
    }

    public void setOrderTicketId(long orderTicketId) {
        this.orderTicketId = orderTicketId;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getShowStartTime() {
        return showStartTime;
    }

    public void setShowStartTime(long showStartTime) {
        this.showStartTime = showStartTime;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getRemark() {
        return remark;
    }

    @Override
    public String toString() {
        return "TickInfo [area=" + area + ", orderId=" + orderId + ", orderTicketId=" + orderTicketId + ", printPrice="
                + price + ", showStartTime=" + showStartTime + ", productName=" + productName + ", remark=" + remark
                + ", seat=" + seat + ", ticketNo=" + ticketNo + ", venueName=" + venueName + "]";
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

}
