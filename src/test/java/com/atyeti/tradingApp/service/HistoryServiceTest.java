package com.atyeti.tradingApp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.atyeti.tradingApp.models.HistoryModel;
import com.atyeti.tradingApp.repository.HistoryRepository;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {HistoryService.class})
@ExtendWith(SpringExtension.class)
class HistoryServiceTest {

    HistoryModel ram = new HistoryModel(1, "ram@gmail.com", 1, "IBM",
                    120, 1, "2020-03-01", "21:19:20", "Buy");
    @MockBean
    private HistoryRepository historyRepository;

    @Autowired
    private HistoryService historyService;



    @Test
    void testTransactionHistory() {
        when(this.historyRepository.findAll()).thenReturn(new ArrayList<HistoryModel>());
        assertTrue(this.historyService.transactionHistory("jane.doe@example.org").isEmpty());
        verify(this.historyRepository).findAll();
    }
    @Test
    void testTransactionHistory4() {
        when(this.historyRepository.findAll()).thenReturn(null);
        assertTrue(this.historyService.transactionHistory("jane.doe@example.org").isEmpty());
        verify(this.historyRepository).findAll();
    }
    @Test
    void testTransactionHistory2() {
        HistoryModel historyModel = new HistoryModel();
        historyModel.setTime("Time");
        historyModel.setCompany_name("Company name");
        historyModel.setIndex(1);
        historyModel.setCompany_id(1);
        historyModel.setUser_id("User id");
        historyModel.setQuantity(0);
        historyModel.setSno(0);
        historyModel.setPrice(0);
        historyModel.setDate("2020-03-01");
        historyModel.setType("Type");

        ArrayList<HistoryModel> historyModelList = new ArrayList<HistoryModel>();
        historyModelList.add(historyModel);
        when(this.historyRepository.findAll()).thenReturn(historyModelList);
        assertTrue(this.historyService.transactionHistory("jane.doe@example.org").isEmpty());
        verify(this.historyRepository).findAll();
    }

    @Test
    void testTransactionHistory3() {
        HistoryModel historyModel = new HistoryModel();
        historyModel.setTime("Time");
        historyModel.setCompany_name("Company name");
        historyModel.setIndex(1);
        historyModel.setCompany_id(1);
        historyModel.setUser_id("User id");
        historyModel.setQuantity(0);
        historyModel.setSno(0);
        historyModel.setPrice(0);
        historyModel.setDate("2020-03-01");
        historyModel.setType("Type");

        HistoryModel historyModel1 = new HistoryModel();
        historyModel1.setTime("jane.doe@example.org");
        historyModel1.setCompany_name("jane.doe@example.org");
        historyModel1.setIndex(1);
        historyModel1.setCompany_id(1);
        historyModel1.setUser_id("jane.doe@example.org");
        historyModel1.setQuantity(0);
        historyModel1.setSno(0);
        historyModel1.setPrice(0);
        historyModel1.setDate("2020-03-01");
        historyModel1.setType("jane.doe@example.org");

        ArrayList<HistoryModel> historyModelList = new ArrayList<HistoryModel>();
        historyModelList.add(historyModel1);
        historyModelList.add(historyModel);
        when(this.historyRepository.findAll()).thenReturn(historyModelList);
        assertEquals(1, this.historyService.transactionHistory("jane.doe@example.org").size());
        verify(this.historyRepository).findAll();
    }
}

