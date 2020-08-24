package com.gps.selenium.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.gps.selenium.business.dao.DefeitoDao;
import com.gps.selenium.enums.Domain;
import com.gps.selenium.enums.TipoAtuacao;
import com.gps.selenium.model.Defeito;
import com.gps.selenium.model.Integrante;
import com.gps.selenium.service.DefeitoService;
import com.gps.selenium.service.request.Request;

import br.com.gvt.jeemodelinfra.exception.DAOException;

@Stateless(name = DefeitoService.JNDI_NAME, mappedName = DefeitoService.JNDI_NAME)
public class DefeitoServiceImpl implements DefeitoService {
	
	public static String WELCOME = "http://10.41.252.111:9010/controle-demanda/defectsComments.jsf?";
	public static String DOMINIO_QA = "dominio=QA_QA";
	public static String DOMINIO_DEV = "dominio=DEV";
	public static String PROJECT = "&project=";
	public static String DB_NAME = "&dbname=";
	public static String DEFECT_ID = "&defectId=";
	public static String ANO = "2020_";
	public static String PROJECT_QA = "&projectQA=";
	
	@EJB
	private DefeitoDao defeitoDao;
	private Request request;

	@Override
	public void salvar(Defeito defeito) {
		return;
	}

	@Override
	public List<Defeito> importarPlanilha(final InputStream inputStream) {
		try {
			List<Defeito> defeitos = new ArrayList<Defeito>();
			final OPCPackage pkg = OPCPackage.open(inputStream);
			final XSSFWorkbook wb = new XSSFWorkbook(pkg);
			final Sheet sheet = wb.getSheetAt(0);
			for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
				
				final Row row = sheet.getRow(i);
				if (!this.isRowEmpty(row)) {
					
					Defeito defeito = new Defeito();
					defeito.setBug(row.getCell(0).getStringCellValue());
					defeito.setProjeto(row.getCell(1).getStringCellValue());
					defeitos.add(defeito);
				}
			}
			return defeitos;
		} catch (IOException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", e.getMessage()));
		} catch (InvalidFormatException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", e.getMessage()));
		} 
		return null;
	}
	
	@Override
	public List<Integrante> importarIntegrantes(final InputStream inputStream) {
		try {
			List<Integrante> integrantes = new ArrayList<Integrante>();
			final OPCPackage pkg = OPCPackage.open(inputStream);
			final XSSFWorkbook wb = new XSSFWorkbook(pkg);
			final Sheet sheet = wb.getSheetAt(1);
			for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
				
				final Row row = sheet.getRow(i);
				if (!this.isRowEmpty(row)) {
					
					if (!Objects.isNull(row.getCell(0)) && StringUtils.isNotEmpty(row.getCell(0).getStringCellValue())) {
						Integrante integrante = new Integrante();
						
						integrante.setName(row.getCell(0).getStringCellValue());
						integrante.setType(TipoAtuacao.DESENVOLVIMENTO.getValor());
						integrantes.add(integrante);
					}
					if (!Objects.isNull(row.getCell(1)) && StringUtils.isNotEmpty(row.getCell(1).getStringCellValue())) {
						Integrante integrante = new Integrante();
						
						integrante.setName(row.getCell(1).getStringCellValue());
						integrante.setType(TipoAtuacao.FUNCIONAL.getValor());
						integrantes.add(integrante);
					}
				}
			}
			return integrantes;
		} catch (IOException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", e.getMessage()));
		} catch (InvalidFormatException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", e.getMessage()));
		} 
		return null;
	}

	@SuppressWarnings("static-access")
	@Override
	public Collection<Defeito> buscarAtuacao(Collection<Integrante> integrantes) throws DAOException {

		Collection<Defeito> defeitos = defeitoDao.buscarAtuacao();
		if (CollectionUtils.isNotEmpty(integrantes)) {
			for (Defeito defeito : defeitos) {
			
				String parametros = this.dePara(defeito);
				if (parametros != null) {
					System.out.println(defeito.getBug().concat(" ").concat(defeito.getDomain()));
					this.request.excutePost(WELCOME, parametros, integrantes, defeito);
				}
			}
		}
		return defeitos;
	}
	
	private String dePara(Defeito defeito) {
		
		if(defeito != null) {
			
			if (ANO.concat(Domain.PJ_ATENDIMENTO_B2C.name()).equals(defeito.getDomain())) {
				
				return DOMINIO_QA.concat(PROJECT.concat(defeito.getProjeto()).concat(DB_NAME.concat(Domain.PJ_ATENDIMENTO_B2C.getValor()).concat(DEFECT_ID.concat(defeito.getBug()).concat(PROJECT_QA.concat(defeito.getDomain())))));
			} else if (ANO.concat(Domain.PJ_DIGITAL.name()).equals(defeito.getDomain())) {
				
				return DOMINIO_QA.concat(PROJECT.concat(defeito.getProjeto()).concat(DB_NAME.concat(Domain.PJ_DIGITAL.getValor()).concat(DEFECT_ID.concat(defeito.getBug()).concat(PROJECT_QA.concat(defeito.getDomain())))));
			} else if (ANO.concat(Domain.PJ_EST_ONGOING_B2C.name()).equals(defeito.getDomain())) {
				
				return DOMINIO_QA.concat(PROJECT.concat(defeito.getProjeto()).concat(DB_NAME.concat(Domain.PJ_EST_ONGOING_B2C.getValor()).concat(DEFECT_ID.concat(defeito.getBug()).concat(PROJECT_QA.concat(defeito.getDomain())))));
			} else if (ANO.concat(Domain.PJ_OSS_B2C_FIXA.name()).equals(defeito.getDomain())) {
				
				return DOMINIO_QA.concat(PROJECT.concat(defeito.getProjeto()).concat(DB_NAME.concat(Domain.PJ_OSS_B2C_FIXA.getValor()).concat(DEFECT_ID.concat(defeito.getBug()).concat(PROJECT_QA.concat(defeito.getDomain())))));
			} else if (ANO.concat(Domain.PJ_TRANSF_B2C.name()).equals(defeito.getDomain())) {
				
				return DOMINIO_QA.concat(PROJECT.concat(defeito.getProjeto()).concat(DB_NAME.concat(Domain.PJ_TRANSF_B2C.getValor()).concat(DEFECT_ID.concat(defeito.getBug()).concat(PROJECT_QA.concat(defeito.getDomain())))));
			} else if (ANO.concat(Domain.PP_FIXA_MOVEL.name()).equals(defeito.getDomain())) {
				
				return DOMINIO_QA.concat(PROJECT.concat(defeito.getProjeto()).concat(DB_NAME.concat(Domain.PP_FIXA_MOVEL.getValor()).concat(DEFECT_ID.concat(defeito.getBug()).concat(PROJECT_QA.concat(defeito.getDomain())))));
			} else if (ANO.concat(Domain.REGRESSAO.name()).equals(defeito.getDomain())) {
				
				return DOMINIO_QA.concat(PROJECT.concat(defeito.getProjeto()).concat(DB_NAME.concat(Domain.REGRESSAO.getValor()).concat(DEFECT_ID.concat(defeito.getBug()).concat(PROJECT_QA.concat(defeito.getDomain())))));
			} else if (ANO.concat(Domain.SUSTENTACAO.name()).equals(defeito.getDomain())) {
				
				return DOMINIO_QA.concat(PROJECT.concat(defeito.getProjeto()).concat(DB_NAME.concat(Domain.SUSTENTACAO.getValor()).concat(DEFECT_ID.concat(defeito.getBug()).concat(PROJECT_QA.concat(defeito.getDomain())))));
			} else if (ANO.concat(Domain.PJ_OG_B2B_FINANCAS.name()).equals(defeito.getDomain())) {
				
				return DOMINIO_QA.concat(PROJECT.concat(defeito.getProjeto()).concat(DB_NAME.concat(Domain.PJ_OG_B2B_FINANCAS.getValor()).concat(DEFECT_ID.concat(defeito.getBug()).concat(PROJECT_QA.concat(defeito.getDomain())))));
			} else if (ANO.concat(Domain.PJ_TRANSF_B2B_APOIO.name()).equals(defeito.getDomain())) {
			
				return DOMINIO_QA.concat(PROJECT.concat(defeito.getProjeto()).concat(DB_NAME.concat(Domain.PJ_TRANSF_B2B_APOIO.getValor()).concat(DEFECT_ID.concat(defeito.getBug()).concat(PROJECT_QA.concat(defeito.getDomain())))));
			} else if (Domain.AMDOCS_E2E.name().equals(defeito.getDomain())) {
			
				return DOMINIO_DEV.concat(PROJECT.concat(defeito.getProjeto()).concat(DB_NAME.concat(Domain.AMDOCS_E2E.getValor()).concat(DEFECT_ID.concat(defeito.getBug()).concat(PROJECT_QA.concat(defeito.getDomain())))));
			}
		}
		return null;
	}

	private boolean isCellNotBlank(final Cell cell) {
		
		if (cell != null) {
			cell.setCellType(Cell.CELL_TYPE_STRING);
		} else {
			return Boolean.FALSE;
		}
		
		return cell != null && StringUtils.isNotBlank(cell.getStringCellValue());
	}

	public boolean isRowEmpty(final Row row) {

		if (row == null) {
			return Boolean.TRUE;
		}

		return !this.isCellNotBlank(row.getCell(0)) && !this.isCellNotBlank(row.getCell(1));
	}

}
	