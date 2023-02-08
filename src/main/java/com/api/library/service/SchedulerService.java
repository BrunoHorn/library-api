package com.api.library.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.api.library.model.entity.Loan;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SchedulerService {

	private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";
	
	private final LoanService loanService;
	private final EmailService emailService;
	
	@Value("${application.mail.lateloans.message}") //mensagem do application.propertis
	private String message;
	
	@Scheduled(cron = CRON_LATE_LOANS) //segundo - minuto - hora - dia - mês - ano - cronMaker.com método será executado de acordo com esses parametros
	public void sendMailToLateLoans() {
		List<Loan> allLateLoans = loanService.getAllLateLoans();
		List<String> mailsList = allLateLoans.stream().map(loan -> 
								loan.getCustomerEmail()).collect(Collectors.toList());
		
		emailService.sendEmails(message, mailsList);
	}
}
