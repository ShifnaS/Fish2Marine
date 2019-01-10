package com.smacon.fish2marine.HelperClass;

/**
 * Created by user on 3/17/2017.
 */

public class CartListItem {

    private String ProductId,Quantity,ItemId,Price,ItemsTotal,CutTypeApplicable,CutType,
                    Subtotal,Tax,Shipping,ProductImage,ProductName,OtherName,ItemCount,GrandTotal,SoldBy;
    private String carrier_code,method_code,carrier_title,method_title,amount,base_amount,available,error_message,
            price_excl_tax,price_incl_tax;
    private double BeforeCleaning,AfterCleaning,NetQty;
    public CartListItem() {

    }

    public String getCarrier_code(){
        return carrier_code;
    }

    public void setCarrier_code(String carrier_code){
        this.carrier_code=carrier_code;
    }

    public String getMethod_code(){
        return method_code;
    }

    public void setMethod_code(String method_code){
        this.method_code=method_code;
    }

    public String getCarrier_title() {
        return carrier_title;
    }

    public void setCarrier_title(String carrier_title) {
        this.carrier_title = carrier_title;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSoldBy() {
        return SoldBy;
    }

    public void setSoldBy(String SoldBy) {
        this.SoldBy = SoldBy;
    }

    public String getItemsTotal() {
        return ItemsTotal;
    }

    public void setItemsTotal(String ItemsTotal) {
        this.ItemsTotal = ItemsTotal;
    }

    public String getCutTypeApplicable() {
        return CutTypeApplicable;
    }

    public void setCutTypeApplicable(String CutTypeApplicable) {
        this.CutTypeApplicable = CutTypeApplicable;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String ProductId) {
        this.ProductId = ProductId;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String Quantity) {
        this.Quantity = Quantity;
    }

    public String getItemId() {
        return ItemId;
    }

    public void setItemId(String ItemId) {
        this.ItemId = ItemId;
    }

    public double getNetQty() {
        return NetQty;
    }

    public void setNetQty(double NetQty) {
        this.NetQty = NetQty;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String Price) {
        this.Price = Price;
    }

    public String getCutType() {
        return CutType;
    }

    public void setCutType(String CutType) {
        this.CutType = CutType;
    }

    public double getBeforeCleaning() {
        return BeforeCleaning;
    }

    public void setBeforeCleaning(double BeforeCleaning) {
        this.BeforeCleaning = BeforeCleaning;
    }

    public double getAfterCleaning() {
        return AfterCleaning;
    }

    public void setAfterCleaning(double AfterCleaning) {
        this.AfterCleaning = AfterCleaning;
    }

    public String getSubtotal() {
        return Subtotal;
    }

    public void setSubtotal(String Subtotal) {
        this.Subtotal = Subtotal;
    }

    public String getTax() {
        return Tax;
    }

    public void setTax(String Tax) {
        this.Tax = Tax;
    }

    public String getShipping() {
        return Shipping;
    }

    public void setShipping(String Shipping) {
        this.Shipping = Shipping;
    }

    public String getProductImage() {
        return ProductImage;
    }

    public void setProductImage(String ProductImage) {
        this.ProductImage = ProductImage;
    }

    public String getProductName(){
        return ProductName;
    }
    public void setProductName(String ProductName) {
        this.ProductName = ProductName;
    }

    public String getOtherName(){
        return OtherName;
    }
    public void setOtherName(String OtherName) {
        this.OtherName = OtherName;
    }

    public String getItemCount() {
        return ItemCount;
    }

    public void setItemCount(String ItemCount) {
        this.ItemCount = ItemCount;
    }

    public String getGrandTotal() {
        return GrandTotal;
    }

    public void setGrandTotal(String GrandTotal) {
        this.GrandTotal = GrandTotal;
    }

}