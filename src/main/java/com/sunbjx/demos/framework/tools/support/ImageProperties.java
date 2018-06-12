package com.sunbjx.demos.framework.tools.support;

/**
 * @author sunbjx
 * @since 2018/6/12 15:29
 */
public class ImageProperties {

    /**
     * 图片宽
     */
    private int width;
    /**
     * 图片高
     */
    private int height;

    /**
     * 图片类型
     */
    private String imageType;

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return the imageType
     */
    public String getImageType() {
        return imageType;
    }

    /**
     * @param imageType the imageType to set
     */
    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return super.hashCode();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        return super.equals(obj);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        StringBuffer sb = new StringBuffer();
        sb.append("width[");
        sb.append(this.width);
        sb.append("]height[");
        sb.append(this.height);
        sb.append("]imageType[");
        sb.append(this.imageType);
        sb.append("]");
        return sb.toString();
    }
}
