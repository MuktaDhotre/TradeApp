package com.atyeti.tradingApp.service;

import com.atyeti.tradingApp.models.CompanyModel;
import com.atyeti.tradingApp.repository.CompanyRepository;
import com.atyeti.tradingApp.repository.HistoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;

import javax.validation.constraints.Null;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CompanyService {

    @Autowired
    CompanyRepository companyRepository;

    public Optional<CompanyModel> getOne(int id) {

        return companyRepository.findById(id);
    }

    public Map<String, String> addCompany(CompanyModel companyModel) {
        Map<String, String> response = new HashMap<>();

            companyRepository.save(companyModel);
            response.put("status", "success");



        return response;
    }

    public List<CompanyModel> search() {
        return (List<CompanyModel>) companyRepository.findAll();
    }


    public Map<String, String> deleteCompany(int id) {

        CompanyModel cm = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not exist with id " + id));
        companyRepository.delete(cm);

        Map<String, String> response = new HashMap<String, String>();
        response.put("status", "success");
        return response;

    }

    public ResponseEntity<CompanyModel> updateCompany(int company_id, CompanyModel company1) {

        CompanyModel company=companyRepository.findById(company_id)
                .orElseThrow(()->new RuntimeException("company not exits :"+company_id));

        company.setName(company1.getName());
        company.setOpen_rate(company1.getOpen_rate());
        company.setClose_rate(company1.getClose_rate());
        company.setLeast_rate(company1.getLeast_rate());
        company.setPeak_rate(company1.getPeak_rate());
        company.setYear_low(company1.getYear_low());
        company.setYear_high(company1.getYear_high());
        company.setP_e_ratio(company1.getP_e_ratio());
        company.setMarket_cap(company1.getMarket_cap());
        company.setVolume(company1.getVolume());
        company.setCurrent_rate(company1.getCurrent_rate());

        CompanyModel c = companyRepository.save(company);
        return ResponseEntity.ok(c);

    }


    public ResponseEntity<CompanyModel> getCompanyById(int company_id) {
   CompanyModel company=companyRepository.findById(company_id)
           .orElseThrow(()->new RuntimeException("company not exits :"  +company_id));
           return  ResponseEntity.ok(company);

    }
}