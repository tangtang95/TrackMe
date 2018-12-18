package com.trackme.trackmeapplication.sharedData.network;

import com.trackme.trackmeapplication.home.userHome.HistoryItem;
import com.trackme.trackmeapplication.sharedData.ThirdPartyInterface;
import com.trackme.trackmeapplication.sharedData.User;

import java.util.List;

public interface SharedDataNetworkInterface {

    User getUser(String username);

    ThirdPartyInterface getThirdParty(String mail);

    String getGroupRequestData(String requestID);

    String getIndividualRequestData(String requestID);

    List<HistoryItem> getUserData(String username, String startDate, String endDate);

}
