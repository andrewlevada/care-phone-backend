package com.andrewlevada.carephoneserver;

import com.andrewlevada.carephoneserver.logic.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@EnableAutoConfiguration
public class Requests {
    @Autowired
    private Database database;

    // Users

    @RequestMapping(method = RequestMethod.PUT, path = "/users")
    public void tryToPutUser(@RequestParam String userToken) {
        String uid = Toolbox.getUidFromFirebaseAuthToken(userToken);
        if (uid == null) return;
        if (!database.hasUser(uid)) database.addUser(uid);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/makePro")
    public void makeUserPro(@RequestParam String userToken) {
        String uid = Toolbox.getUidFromFirebaseAuthToken(userToken);
        if (uid == null) return;
        database.makeUserPro(uid);
    }
    
    // Whitelist

    @RequestMapping(method = RequestMethod.GET, path = "/whitelist")
    public List<PhoneNumber> getWhitelist(@RequestParam String userToken) {
        String uid = Toolbox.getUidFromFirebaseAuthToken(userToken);
        if (uid == null) return null;
        return database.getWhitelist(uid);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/whitelist")
    public void putWhitelist(@RequestParam String userToken, @RequestParam String phone, @RequestParam String label) {
        String uid = Toolbox.getUidFromFirebaseAuthToken(userToken);
        if (uid == null) return;
        database.addWhitelistRecord(uid, new PhoneNumber(phone, label));
    }

    @RequestMapping(method = RequestMethod.POST, path = "/whitelist")
    public void postWhitelist(@RequestParam String userToken, @RequestParam String prevPhone, @RequestParam String phone, @RequestParam String label) {
        String uid = Toolbox.getUidFromFirebaseAuthToken(userToken);
        if (uid == null) return;
        database.editWhitelistRecord(uid, prevPhone, new PhoneNumber(phone, label));
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/whitelist")
    public void deleteWhitelist(@RequestParam String userToken, @RequestParam String phone) {
        String uid = Toolbox.getUidFromFirebaseAuthToken(userToken);
        if (uid == null) return;
        database.deleteWhitelistRecord(uid, phone);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/whitelist/r")
    public List<PhoneNumber> getWhitelistR(@RequestParam String userToken, @RequestParam String rUid) {
        String uid = Toolbox.getUidFromFirebaseAuthToken(userToken);
        if (uid == null) return null;
        if (!database.checkRemote(uid, rUid)) return null;
        return database.getWhitelist(rUid);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/whitelist/r")
    public void putWhitelistR(@RequestParam String userToken, @RequestParam String rUid, @RequestParam String phone, @RequestParam String label) {
        String uid = Toolbox.getUidFromFirebaseAuthToken(userToken);
        if (uid == null) return;
        if (!database.checkRemote(uid, rUid)) return;
        database.addWhitelistRecord(rUid, new PhoneNumber(phone, label));
    }

    @RequestMapping(method = RequestMethod.POST, path = "/whitelist/r")
    public void postWhitelistR(@RequestParam String userToken, @RequestParam String rUid, @RequestParam String prevPhone, @RequestParam String phone, @RequestParam String label) {
        String uid = Toolbox.getUidFromFirebaseAuthToken(userToken);
        if (uid == null) return;
        if (!database.checkRemote(uid, rUid)) return;
        database.editWhitelistRecord(rUid, prevPhone, new PhoneNumber(phone, label));
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/whitelist/r")
    public void deleteWhitelistR(@RequestParam String userToken, @RequestParam String rUid, @RequestParam String phone) {
        String uid = Toolbox.getUidFromFirebaseAuthToken(userToken);
        if (uid == null) return;
        if (!database.checkRemote(uid, rUid)) return;
        database.deleteWhitelistRecord(rUid, phone);
    }

    // Whitelist State

    @RequestMapping(method = RequestMethod.GET, path = "/whitelist/state")
    public Boolean getWhitelistState(@RequestParam String userToken) {
        String uid = Toolbox.getUidFromFirebaseAuthToken(userToken);
        if (uid == null) return null;
        return database.getWhitelistState(uid);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/whitelist/state")
    public void postWhitelistState(@RequestParam String userToken, @RequestParam Boolean state) {
        String uid = Toolbox.getUidFromFirebaseAuthToken(userToken);
        if (uid == null) return;
        database.setWhitelistState(uid, state);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/whitelist/state/r")
    public Boolean getWhitelistStateR(@RequestParam String userToken, @RequestParam String rUid) {
        String uid = Toolbox.getUidFromFirebaseAuthToken(userToken);
        if (uid == null) return null;
        if (!database.checkRemote(uid, rUid)) return null;
        return database.getWhitelistState(rUid);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/whitelist/state/r")
    public void postWhitelistStateR(@RequestParam String userToken, @RequestParam String rUid, @RequestParam Boolean state) {
        String uid = Toolbox.getUidFromFirebaseAuthToken(userToken);
        if (uid == null) return;
        if (!database.checkRemote(uid, rUid)) return;
        database.setWhitelistState(rUid, state);
    }

    // Statistics

    @RequestMapping(method = RequestMethod.GET, path = "/statistics")
    public StatisticsPack getStatisticsPack(@RequestParam String userToken) {
        String uid = Toolbox.getUidFromFirebaseAuthToken(userToken);
        if (uid == null) return null;

        return DataProcessing.generateStatisticsPack(database, uid);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/statistics/r")
    public StatisticsPack getStatisticsPackR(@RequestParam String userToken, @RequestParam String rUid) {
        String uid = Toolbox.getUidFromFirebaseAuthToken(userToken);
        if (uid == null) return null;
        if (!database.checkRemote(uid, rUid)) return null;

        return DataProcessing.generateStatisticsPack(database, rUid);
    }

    // Log

    @RequestMapping(method = RequestMethod.GET, path = "/log")
    public List<LogRecord> getLog(@RequestParam String userToken, @RequestParam int limit, @RequestParam int offset) {
        String uid = Toolbox.getUidFromFirebaseAuthToken(userToken);
        if (uid == null) return null;
        return database.getLimitedLogRecords(uid, limit, offset);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/log")
    public void putLog(@RequestParam String userToken, @RequestParam String phoneNumber, @RequestParam long startTimestamp, @RequestParam int secondsDuration, @RequestParam int type) {
        String uid = Toolbox.getUidFromFirebaseAuthToken(userToken);
        if (uid == null) return;
        database.addLogRecord(uid, phoneNumber, startTimestamp, secondsDuration, type);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/log/r")
    public List<LogRecord> getLogR(@RequestParam String userToken, @RequestParam String rUid, @RequestParam int limit, @RequestParam int offset) {
        String uid = Toolbox.getUidFromFirebaseAuthToken(userToken);
        if (uid == null) return null;
        if (!database.checkRemote(uid, rUid)) return null;
        return database.getLimitedLogRecords(rUid, limit, offset);
    }

    // Cared List

    @RequestMapping(method = RequestMethod.GET, path = "/caredList")
    public List<CaredUser> getCaredList(@RequestParam String userToken) {
        String uid = Toolbox.getUidFromFirebaseAuthToken(userToken);
        if (uid == null) return null;
        return database.getCaredList(uid);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/caredList/v2")
    public List<CaredUser2> getCaredList2(@RequestParam String userToken) {
        String uid = Toolbox.getUidFromFirebaseAuthToken(userToken);
        if (uid == null) return null;
        return database.getCaredList2(uid);
    }

    // Link

    @RequestMapping(method = RequestMethod.PUT, path = "/link")
    public String putLink(@RequestParam String userToken) {
        String uid = Toolbox.getUidFromFirebaseAuthToken(userToken);
        if (uid == null) return null;
        return database.addLinkRequest(uid);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/link")
    public void deleteLink(@RequestParam String userToken) {
        String uid = Toolbox.getUidFromFirebaseAuthToken(userToken);
        if (uid == null) return;
        database.deleteLinkRequestByUid(uid);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/link")
    public int postLink(@RequestParam String userToken, @RequestParam String code) {
        String uid = Toolbox.getUidFromFirebaseAuthToken(userToken);
        if (uid == null) return 0;
        return database.tryToLinkCaretaker(uid, code);
    }

    // Other

    @RequestMapping(method = RequestMethod.PUT, path = "/bugreport")
    public void putBugReport(@RequestParam String userToken, @RequestParam String subject, @RequestParam String message, @RequestParam String info) {
        String uid = Toolbox.getUidFromFirebaseAuthToken(userToken);
        if (uid == null) return;
        database.addBugReport(uid, subject, message, info);
    }
}
