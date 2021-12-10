package com.atyeti.tradingApp.service;

import com.atyeti.tradingApp.models.CompanyModel;
import com.atyeti.tradingApp.models.HistoryModel;
import com.atyeti.tradingApp.models.MySharesModel;
import com.atyeti.tradingApp.models.UserModel;
import com.atyeti.tradingApp.repository.*;
import com.atyeti.tradingApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class MyShareService {

    @Autowired
    MySharesRepository mySharesRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    HistoryRepository historyRepository;


    public List<MySharesModel> myShare(String email) {
        List<MySharesModel> allShares = (List<MySharesModel>) mySharesRepository.findAll();
        List<MySharesModel> myShares = new ArrayList<MySharesModel>();
        try {
            Iterator<MySharesModel> iter = allShares.iterator();
            while (iter.hasNext()) {
                MySharesModel ms = (MySharesModel) iter.next();
                if (email.equals(ms.getUser_id())) {
                    System.out.print(ms);
                    myShares.add(ms);
                }
            }
        } catch (Exception e) {
            System.out.print("Exception : ");
            System.out.print(e.getMessage());
        }
        return myShares;
    }


    public Map<String, String> buy(Map<String, String> userInput) {
        String email = (String) userInput.get("email");
        int companyId = Integer.parseInt(userInput.get("companyId").toString());
        int qty = Integer.parseInt(userInput.get("quantity").toString());
//        int current_rate=Integer.parseInt(userInput.get("current_rate").toString());

        Date localDate = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String date = formatter.format(localDate);

        LocalTime localTime = java.time.LocalTime.now();
        String time = localTime + "";
        time = time.substring(0, 8);

        HashMap<String, String> map = new HashMap<>();

        List<MySharesModel> allShares = (List<MySharesModel>) mySharesRepository.findAll();
        List<MySharesModel> myShares = new ArrayList<MySharesModel>();

        try {
            UserModel userModel = userRepository.findByEmail(email);
            CompanyModel companyModel = companyRepository.findById(companyId).orElseThrow(() -> new RuntimeException("Company not Found"));

            Iterator<MySharesModel> iter2 = allShares.iterator();
            while (iter2.hasNext()) {
                MySharesModel ms = (MySharesModel) iter2.next();
                if (email.equals(ms.getUser_id())) {
                    if (ms.getCompany_id() == companyId)
                        myShares.add(ms);
                }
            }
            
            if (companyModel.getVolume() < qty) {
                map.put("status", "insufficient quantity");
                return map;
            }

            if (userModel.getAmount_left() <= (qty * companyModel.getCurrent_rate())) {
                map.put("status", "insufficient balance");
                return map;
            } else if (!myShares.isEmpty()) {
                int userAmount = userModel.getAmount_left();
                int currentAmount = userAmount - qty * companyModel.getCurrent_rate();

                int userQty = myShares.get(0).getQuantity();
                int currentQty = userQty + qty;

                int currentVol = companyModel.getVolume() - qty;

                myShares.get(0).setQuantity(currentQty);
                myShares.get(0).setBought_rate(companyModel.getCurrent_rate());
                myShares.get(0).setDate(date);
                myShares.get(0).setTime(time);
//                myShares.get(0).setCurrent_rate(current_rate);
                mySharesRepository.save(myShares.get(0));

                companyModel.setVolume(currentVol);
                companyRepository.save(companyModel);

                userModel.setAmount_left(currentAmount);
                userRepository.save(userModel);

                HistoryModel history = new HistoryModel(myShares.get(0).getSno(), email, myShares.get(0).getCompany_id(),
                        myShares.get(0).getName(), -companyModel.getCurrent_rate() * qty, qty, date, time, "Buy");

                historyRepository.save(history);

                map.put("status", "success");
                return map;
            } else {
                int userAmount = userModel.getAmount_left();
                int currentAmount = userAmount - qty * companyModel.getCurrent_rate();
                int currentVol = companyModel.getVolume() - qty;
                userModel.setAmount_left(currentAmount);
                userRepository.save(userModel);

                MySharesModel msm = new MySharesModel(companyId, companyModel.getName(), email,
                        companyModel.getOpen_rate(), companyModel.getClose_rate(), companyModel.getPeak_rate(),
                        companyModel.getLeast_rate(), companyModel.getCurrent_rate(), qty,
                        companyModel.getCurrent_rate(), companyModel.getYear_low(), companyModel.getYear_high(),
                        companyModel.getMarket_cap(), companyModel.getP_e_ratio(), companyModel.getVolume(), date, time);
                mySharesRepository.save(msm);

                companyModel.setVolume(currentVol);
                companyRepository.save(companyModel);

                List<MySharesModel> records = (List<MySharesModel>) mySharesRepository.findAll();
                List<MySharesModel> transaction = new ArrayList<MySharesModel>();

                Iterator<MySharesModel> iter4 = records.iterator();
                while (iter4.hasNext()) {
                    MySharesModel ms = (MySharesModel) iter4.next();
                    if (ms.getUser_id().equals(email) && ms.getCompany_id() == companyId) {
                        transaction.add(ms);
                    }
                }

                MySharesModel insert = transaction.get(0);

                HistoryModel history = new HistoryModel(insert.getSno(), email, insert.getCompany_id(), insert.getName(),
                        -insert.getCurrent_rate() * qty, qty, date, time, "Buy");
                historyRepository.save(history);
            }
        } catch (Exception e) {
            map.put("status", e.getMessage());
            return map;
        }
        map.put("status", "success");
        return map;
    }

    //module for selling share
    public Map<String, String> sell(Map<String, String> payload) {
        String email = (String) payload.get("email");
        int companyId = Integer.parseInt(payload.get("companyId").toString());
        int qty = Integer.parseInt(payload.get("quantity").toString());
//        int current_rate=Integer.parseInt(payload.get("current_rate").toString());

        HashMap<String, String> map = new HashMap<>();

        Date localDate = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String date = formatter.format(localDate);

        LocalTime localTime = java.time.LocalTime.now();
        String time = localTime + "";
        time = time.substring(0, 8);
        List<UserModel> users = (List<UserModel>) userRepository.findAll();
        List<UserModel> user = new ArrayList<UserModel>();

        List<MySharesModel> allShares = (List<MySharesModel>) mySharesRepository.findAll();
        List<MySharesModel> myShares = new ArrayList<MySharesModel>();

        List<CompanyModel> allCompany = (List<CompanyModel>) companyRepository.findAll();
        List<CompanyModel> company = new ArrayList<CompanyModel>();

        try {
            Iterator<UserModel> iter = users.iterator();
            while (iter.hasNext()) {
                UserModel us = (UserModel) iter.next();
                if (email.equals(us.getEmail()))
                    user.add(us);
            }

            Iterator<MySharesModel> iter2 = allShares.iterator();
            while (iter2.hasNext()) {
                MySharesModel ms = (MySharesModel) iter2.next();
                if (email.equals(ms.getUser_id())) {
                    if (ms.getCompany_id() == companyId)
                        myShares.add(ms);
                }
            }

            Iterator<CompanyModel> iter3 = allCompany.iterator();
            while (iter3.hasNext()) {
                CompanyModel cm = (CompanyModel) iter3.next();
                if (companyId == cm.getCompany_id())
                    company.add(cm);
            }

            int userAmount = user.get(0).getAmount_left();
            int currentAmount = userAmount + qty * company.get(0).getCurrent_rate();

            int userQty = myShares.get(0).getQuantity();
            int currentQty = userQty - qty;

            int currentVol = company.get(0).getVolume() + qty;

            if (currentQty == 0) {
                mySharesRepository.delete(myShares.get(0));
            } else {
                myShares.get(0).setDate(date);
                myShares.get(0).setTime(time);
                myShares.get(0).setQuantity(currentQty);
//                myShares.get(0).setCurrent_rate(current_rate);
                mySharesRepository.save(myShares.get(0));
            }

            company.get(0).setVolume(currentVol);
            companyRepository.save(company.get(0));


            user.get(0).setAmount_left(currentAmount);
            userRepository.save(user.get(0));

            HistoryModel history = new HistoryModel(myShares.get(0).getSno(), myShares.get(0).getUser_id(),
                    company.get(0).getCompany_id(), company.get(0).getName(), qty * company.get(0).getCurrent_rate(),
                    qty, date, time, "Sell");

            historyRepository.save(history);
            map.put("status", "success");
            return map;

        } catch (Exception e) {
            map.put("status", e.getMessage());
            return map;
        }
    }

}
