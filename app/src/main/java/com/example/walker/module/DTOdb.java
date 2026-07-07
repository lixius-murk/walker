package com.example.walker.module;

public class DTOdb {

    public static class PlanRequest{
        private Long id;
        private String name;
        private String date;



        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
        public Long getId() {
                return id;
            }

            public void setId(Long id) {
                this.id = id;
            }
    }
    public static class PlanResponse{
        private Long id;
        private String name;
        private String date;

        PlanResponse(){}


        public PlanResponse(Long id, String name, String date) {
            this.id = id;
            this.name = name;
            this.date = date;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
    public static class PlaceRequest{
        private Long id;
        private String name;



        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
    public static class PlaceResponse{
        private Long id;
        private String name;

        PlaceResponse(){}


        public PlaceResponse(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }


    }



}
