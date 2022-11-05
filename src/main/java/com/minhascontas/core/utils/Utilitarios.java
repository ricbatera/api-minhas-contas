package com.minhascontas.core.utils;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Utilitarios {
	
	public static OffsetDateTime primeiroDiaDoMes (String data) {
		data = data.concat("T00:00:00+00:00");
		OffsetDateTime d = OffsetDateTime.parse(data);
		return d.withDayOfMonth(1);
	}
	
	public static OffsetDateTime ultimoDiadomes(String data) {
		LocalDate parse = LocalDate.parse(data);
		parse = parse.withDayOfMonth(parse.lengthOfMonth());
		String d = parse.toString().concat("T00:00:00+00:00");
		return OffsetDateTime.parse(d);
	}
	
	public static List<OffsetDateTime> dataInicialFinalAtual(String data){
		//último dia do mês
		List<OffsetDateTime> lista = new ArrayList<>();
		LocalDate parse = LocalDate.parse(data);
		LocalDate ultimoDate = parse.withDayOfMonth(parse.lengthOfMonth());
		String ultimo = ultimoDate.toString().concat("T00:00:00+03:00");

		//primeiro dia do mês
		data = data.concat("T00:00:00+03:00");
		OffsetDateTime primeiro = OffsetDateTime.parse(data);
		
		lista.add(primeiro.withDayOfMonth(1)); // inclui o primeiro dia do mês
		lista.add(OffsetDateTime.parse(ultimo)); //inclui o ultimo dia do mês
		lista.add(OffsetDateTime.parse(data)); // inclui o dia atual
		
		return lista;
		
	}
	
	public static OffsetDateTime getDataVencimentoCartao(String data, Integer diaVencimento) {
		OffsetDateTime dataVencimento = OffsetDateTime.parse(data.concat("T00:00:00+03:00"));
		return dataVencimento.withDayOfMonth(diaVencimento);
	}
	
	public static LocalDate getDataVencimentoCartaoLocalDate(String data, Integer diaVencimento) {
		LocalDate vencimento = LocalDate.parse(data);		
		return vencimento.withDayOfMonth(diaVencimento);
	}
	
	public static List<LocalDate> getDataInicialDataFinalLocalDate(int mes){
		List<LocalDate> datas = new ArrayList<>();
		LocalDate dataBase = LocalDate.now();
		
		dataBase = dataBase.withMonth(mes);
		
		datas.add(dataBase.withDayOfMonth(1)); // seta primeiro dia do mes
		datas.add(dataBase.withDayOfMonth(dataBase.lengthOfMonth())); //seta ultimo dia do mes
		
		return datas;
	}
	
	public static List<LocalDate> getDatasInicialFinalAtualLocalDate(String data){
		List<LocalDate> datas = new ArrayList<>();
		LocalDate dataBase = LocalDate.parse(data);		
				
		datas.add(dataBase.withDayOfMonth(1)); // seta primeiro dia do mes
		datas.add(dataBase.withDayOfMonth(dataBase.lengthOfMonth())); //seta ultimo dia do mes
		datas.add(dataBase);
		
		return datas;
	}
	
	public static List<OffsetDateTime> getDataInicialDataFinal(int mes){
		List<OffsetDateTime> datas = new ArrayList<>();
		OffsetDateTime dataBase = OffsetDateTime.now();
		dataBase = dataBase.withMonth(mes);
		
		String convert = dataBase.toString().substring(0, 10);
		LocalDate ld = LocalDate.parse(convert);
		ld = ld.withDayOfMonth(ld.lengthOfMonth());
		
		convert = ld.toString().concat(dataBase.toString().substring(10));
		
		datas.add(dataBase.withDayOfMonth(1)); // seta o primeiro dia do mes
		datas.add(OffsetDateTime.parse(convert)); // seta o ultimo dia do mes
		
		return datas;
		
	}

}
