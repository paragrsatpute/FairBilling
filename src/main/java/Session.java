import java.time.LocalTime;

public class Session {


        private String userName;
        private LocalTime startTime;
        private LocalTime endTime;

        public Session() {}

        public Session(String userName, LocalTime startTime, LocalTime endTime) {
            this.userName = userName;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public String getUserName() {
            return userName;
        }
        public void setUserName(String userName) {
            this.userName = userName;
        }
        public LocalTime getStartTime() {
            return startTime;
        }
        public void setStartTime(LocalTime startTime) {
            this.startTime = startTime;
        }
        public LocalTime getEndTime() {
            return endTime;
        }
        public void setEndTime(LocalTime endTime) {
            this.endTime = endTime;
        }
}
