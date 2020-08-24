package com.gps.selenium.bean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Collection;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;

import org.apache.commons.collections4.CollectionUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gps.selenium.model.ArquivoUpload;
import com.gps.selenium.model.Defeito;
import com.gps.selenium.model.Integrante;
import com.gps.selenium.service.DefeitoService;
import com.gps.selenium.service.impl.DefeitoServiceImpl;

import br.com.gvt.jeemodelinfra.exception.DAOException;

@ManagedBean(name = "uploadArquivoBean")
@ViewScoped
public class UploadArquivoBean {
	
	private static final String MENSAGEM_SUCESSO = "Operação realizada com sucesso!";
	
	@EJB
	private DefeitoService defeitoService;
	
	private ArquivoUpload arquivoUpload = new ArquivoUpload();

	@ManagedProperty(name = "defeitoServiceImpl", value = "#{defeitoServiceImpl}")
	private DefeitoServiceImpl defeitoServiceImpl;

	private Part arquivo;
	private Collection<Defeito> defeitos;
	private Collection<Integrante> integrantes;
	private UploadedFile fileUpload;
	private StreamedContent fileDownload;
	
	public void importarDefeitos(final FileUploadEvent event) {

		try {

			this.fileUpload = event.getFile();
			this.defeitos = this.defeitoService.importarPlanilha(event.getFile().getInputstream());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso!", MENSAGEM_SUCESSO));
			
		} catch (final Exception e) {
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", e.getMessage()));
		}
	}
	
	public void importarIntegrantes(final FileUploadEvent event) {
		
		try {
			
			this.fileUpload = event.getFile();
			this.integrantes = this.defeitoService.importarIntegrantes(event.getFile().getInputstream());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso!", MENSAGEM_SUCESSO));
			
		} catch (final Exception e) {
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", e.getMessage()));
		}
	}
	
	public void buscarAtuacao() throws DAOException, IOException {
		
		if(CollectionUtils.isNotEmpty(this.integrantes)) {
			
			this.defeitos = this.defeitoService.buscarAtuacao(this.integrantes);
		}
	}
	
	public void limpar() {

		this.fileUpload = null;
		this.fileDownload = null;
	}
	
	public ArquivoUpload getArquivoUpload() {
		return arquivoUpload;
	}

	public void setArquivoUpload(ArquivoUpload arquivoUpload) {
		this.arquivoUpload = arquivoUpload;
	}

	public DefeitoServiceImpl getDefeitoServiceImpl() {
		return defeitoServiceImpl;
	}

	public void setDefeitoServiceImpl(DefeitoServiceImpl defeitoServiceImpl) {
		this.defeitoServiceImpl = defeitoServiceImpl;
	}

	public Part getArquivo() {
		return arquivo;
	}

	public void setArquivo(Part arquivo) {
		this.arquivo = arquivo;
	}

	public byte[] toByteArrayUsingJava(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int reads = is.read();
		while (reads != -1) {
			baos.write(reads);
			reads = is.read();
		}
		return baos.toByteArray();
	}

	public UploadedFile getFileUpload() {
		return fileUpload;
	}

	public void setFileUpload(UploadedFile fileUpload) {
		this.fileUpload = fileUpload;
	}

	public StreamedContent getFileDownload() {
		return fileDownload;
	}

	public void setFileDownload(StreamedContent fileDownload) {
		this.fileDownload = fileDownload;
	}

	public Collection<Defeito> getDefeitos() {
		return defeitos;
	}

	public void setDefeitos(Collection<Defeito> defeitos) {
		this.defeitos = defeitos;
	}

	public Collection<Integrante> getIntegrantes() {
		return integrantes;
	}

	public void setIntegrantes(Collection<Integrante> integrantes) {
		this.integrantes = integrantes;
	}

}
