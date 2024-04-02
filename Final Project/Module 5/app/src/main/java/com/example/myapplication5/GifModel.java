package com.example.myapplication5;

public class GifModel {
//
//    private String type;
//    private String id;
//    private String url;
//    private Images images;
//
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getUrl() {
//        return url;
//    }
//
//    public void setUrl(String url) {
//        this.url = url;
//    }
//
//    public Images getImages() {
//        return images;
//    }
//
//    public void setImages(Images images) {
//        this.images = images;
//    }
//
//
//    public static class Images {
//        private FixedHeight fixed_height;
//
//        public static class FixedHeight {
//            private String url;
//
//            public String getUrl() {
//                return url;
//            }
//
//            public void setUrl(String url) {
//                this.url = url;
//            }
//
//        }
//    }
    private String gifUrl;

    public GifModel(String gifUrl) {
        this.gifUrl = gifUrl;
    }

    public String getGifUrl() {
        return gifUrl;
    }
}
