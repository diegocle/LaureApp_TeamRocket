package it.uniba.dib.sms222312.modelli.menuHome;

public class cardHome {
        private int image;
        private String title;
        private String activityName;

        public cardHome(int image, String title, String activityName) {
            this.image = image;
            this.title = title;
            this.activityName = activityName;
        }

        public int getImage() {
            return image;
        }

        public void setImage(int image) {
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

    public String getactivityName() {
        return activityName;
    }

    public void setactivityName(String descrizione) {
        this.activityName = descrizione;
    }
}
