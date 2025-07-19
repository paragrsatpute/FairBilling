import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FairBilling {

    protected List<String> readDataToList(String fileName) {
        List<String> dataList = new ArrayList<>();
        try {
            dataList = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new FairBillingException("File error ", e);
        }
        return dataList;
    }

    public boolean verifyDataAsRequested(String data) {

        if ((data == null) || data.isEmpty()) {
            return false;
        }

        String patternString = "(\\d\\d):(\\d\\d):(\\d\\d) [a-zA-Z0-9]{7} .*";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(data);

        if (matcher.find()) {
            return true;
        } else {
            return false;
        }
    }

    protected List<BillingData> breakTheData(List<String> dataList) {

        List<BillingData> billingDataList = new ArrayList<>();
        for (String data : dataList) {

            boolean isDataCorrect = verifyDataAsRequested(data);
            if (isDataCorrect) {
                String[] result = data.split(" ");
                BillingData b = new BillingData();
                b.setTime(result[0]);
                b.setUserName(result[1]);
                b.setAction(result[2]);
                billingDataList.add(b);
            }
        }
        return billingDataList;
    }

    protected List<Map<String, Object>> processData(List<String> dataList) {

        List<BillingData> billingDataList = breakTheData(dataList);

        //startTimeOfFile for where there is an End with no possible matching start, the start time should be assumed to be the earliest time of any record in the file
        LocalTime startTimeOfFile = LocalTime.parse(billingDataList.get(0).getTime(), DateTimeFormatter.ofPattern("HH:mm:ss"));
        //endTimeOfFile for where there is a Start with no possible matching End, the end time should be assumed to be the latest time of any record in the file
        LocalTime endTimeOfFile = LocalTime.parse(billingDataList.get(billingDataList.size() - 1).getTime(), DateTimeFormatter.ofPattern("HH:mm:ss"));


        List<Session> sessionList = new ArrayList<>();

        for (BillingData billingData : billingDataList) {
            boolean isSessionAvailable = false;
            LocalTime lineTime = LocalTime.parse(billingData.getTime(), DateTimeFormatter.ofPattern("HH:mm:ss"));

            //if it is a session start action, it will add new session
            if (billingData.getAction().equalsIgnoreCase("START")) {
                Session session = new Session();
                session.setUserName(billingData.getUserName());
                session.setStartTime(lineTime);
                sessionList.add(session);
            }

            //if it is session end action, it will check first if any session with start action is available having same user name in session list where end time is null, if available then
            // it will update it with end time, if not available then it will create new session.
            if (billingData.getAction().equalsIgnoreCase("END")) {
                for (Session session : sessionList) {
                    if (session.getEndTime() == null && session.getUserName().equalsIgnoreCase(billingData.getUserName())) {
                        session.setEndTime(lineTime);
                        isSessionAvailable = true;
                        break;
                    }
                }

                if (isSessionAvailable == false) {
                    Session session = new Session();
                    session.setUserName(billingData.getUserName());
                    session.setEndTime(lineTime);
                    sessionList.add(session);
                }
            }
        }

        //adding start and end time for no possible matching session starts or stops
        for (Session session : sessionList) {
            if (session.getStartTime() == null) {
                session.setStartTime(startTimeOfFile);
            }
            if (session.getEndTime() == null) {
                session.setEndTime(endTimeOfFile);
            }
        }

        Map<String, List<Session>> userSession = new LinkedHashMap<>();
        List<String> userNameList = sessionList.stream().map(Session::getUserName).distinct().collect(Collectors.toList());
        for (String userName : userNameList) {
            userSession.put(userName, sessionList.stream().filter(u -> u.getUserName().equalsIgnoreCase(userName)).collect(Collectors.toList()));
        }

        List<Map<String, Object>> userSessionResultList = new ArrayList<>();
        for (String userName : userSession.keySet()) {
            int totalDurationInSec = 0;
            int numberOfSessions = 0;
            for (Session session : userSession.get(userName)) {
                numberOfSessions++;
                totalDurationInSec += Duration.between(session.getStartTime(), session.getEndTime()).getSeconds();
            }
            Map<String, Object> userSessionResult = new HashMap<>();
            userSessionResult.put("userName", userName);
            userSessionResult.put("numberOfSessions", numberOfSessions);
            userSessionResult.put("totalDurationInSec", totalDurationInSec);
            userSessionResultList.add(userSessionResult);
        }

        return userSessionResultList;
    }

    public static void main(String[] args) throws IOException {

        try {
            if (args.length < 1) {
                System.out.println("Wrong arguments, please enter file path.");
            } else {
                String fileName = args[0];

                FairBilling fairBilling = new FairBilling();

                //reading data from file to list
                List<String> dataList = fairBilling.readDataToList(fileName);

                //process the data of file to get session data as per user
                List<Map<String, Object>> userSessionResultList = fairBilling.processData(dataList);

                for (Map<String, Object> userSessionResult : userSessionResultList) {
                    System.out.println(userSessionResult.get("userName") + " " + userSessionResult.get("numberOfSessions") + " " + userSessionResult.get("totalDurationInSec"));
                }

            }
        } catch (Exception e) {
            throw new FairBillingException("Error - ", e);
        }
    }
}