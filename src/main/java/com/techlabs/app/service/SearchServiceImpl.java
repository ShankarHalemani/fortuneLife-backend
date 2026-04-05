package com.techlabs.app.service;

import com.techlabs.app.dto.SearchItemDto;
import com.techlabs.app.dto.SearchResultDto;
import com.techlabs.app.entity.Agent;
import com.techlabs.app.entity.Customer;
import com.techlabs.app.entity.InsurancePolicy;
import com.techlabs.app.entity.InsuranceScheme;
import com.techlabs.app.repository.AgentRepository;
import com.techlabs.app.repository.CustomerRepository;
import com.techlabs.app.repository.InsurancePolicyRepository;
import com.techlabs.app.repository.SchemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    private static final int MAX_RESULTS = 10;

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AgentRepository agentRepository;
    @Autowired
    private InsurancePolicyRepository policyRepository;
    @Autowired
    private SchemeRepository schemeRepository;

    @Override
    public SearchResultDto search(String keyword) {
        PageRequest limit = PageRequest.of(0, MAX_RESULTS);
        SearchResultDto result = new SearchResultDto();

        result.setCustomers(customerRepository.searchByKeyword(keyword, limit).stream()
                .map(this::mapCustomer).collect(Collectors.toList()));

        result.setAgents(agentRepository.searchByKeyword(keyword, limit).stream()
                .map(this::mapAgent).collect(Collectors.toList()));

        result.setPolicies(policyRepository.searchByKeyword(keyword, limit).stream()
                .map(this::mapPolicy).collect(Collectors.toList()));

        result.setSchemes(schemeRepository.searchByKeyword(keyword, limit).stream()
                .map(this::mapScheme).collect(Collectors.toList()));

        return result;
    }

    private SearchItemDto mapCustomer(Customer c) {
        String name = c.getUser().getFirstName() + " " + c.getUser().getLastName();
        return new SearchItemDto(
                String.valueOf(c.getId()),
                "CUSTOMER",
                name,
                c.getUser().getEmail());
    }

    private SearchItemDto mapAgent(Agent a) {
        String name = a.getUser().getFirstName() + " " + a.getUser().getLastName();
        return new SearchItemDto(
                String.valueOf(a.getId()),
                "AGENT",
                name,
                a.getUser().getEmail());
    }

    private SearchItemDto mapPolicy(InsurancePolicy p) {
        String customerName = p.getCustomer().getUser().getFirstName() + " " + p.getCustomer().getUser().getLastName();
        return new SearchItemDto(
                p.getId(),
                "POLICY",
                "Policy " + p.getId(),
                customerName + " - " + p.getInsuranceScheme().getSchemeName() + " (" + p.getPolicyStatus() + ")");
    }

    private SearchItemDto mapScheme(InsuranceScheme s) {
        return new SearchItemDto(
                String.valueOf(s.getId()),
                "SCHEME",
                s.getSchemeName(),
                s.getActive() ? "Active" : "Inactive");
    }
}
