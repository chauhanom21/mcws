package com.eclat.mcws.persistence.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.eclat.mcws.util.comparator.ReviewWorklistComparator;

@Entity
@Table(name = "worklist")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class WorkListItem implements com.eclat.mcws.persistence.entity.Entity {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "account_number")
	private String accountNumber;

	@Column(name = "mr_number")
	private String mrNumber;

	@Column(name = "patient_name")
	private String patientName;

	@Column(name = "los")
	private Integer los;

	@Column(name = "tat")
	private Integer tat;

	@Column(name = "dollar_amount")
	private Integer dollarAmount;

	@Column(name = "status")
	private String status;

	@Column(name = "is_audited")
	private Boolean audited;

	@Column(name = "coders_id")
	private Integer coderId;

	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "client_id")
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private ClientDetails clientDetails;

	@Column(name = "chart_spl")
	private String chartSpl;

	@Column(name = "discharged_date")
	private Date dischargedDate;

	@Column(name = "admitted_date")
	private Date admittedDate;

	@Column(name = "updated_date")
	private Timestamp updatedDate;

	@Column(name = "audited_date")
	private Timestamp auditDate;

	@Column(name = "effort_metric")
	private double effortMetric;

	@Column(name = "uploaded_date")
	private Timestamp uploadedDate;

	@Column(name = "insurance")
	private String insurance;

	@Column(name = "service_type")
	private String serviceType;

	@Column(name = "campus_code")
	private String campusCode;

	@Column(name = "is_used_for_auto_em_process")
	private Boolean isUsedForAutoEMProcess;

	@Version
	@Column(name = "version")
	private Integer version;

	@Transient
	private String comments;

	@Transient
	private String clientName;

	@Transient
	private Date codedDate;

	@Transient
	private String coderName;

	@Transient
	private String chartType;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "workListItem", cascade = CascadeType.ALL)
	@Fetch(FetchMode.JOIN)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<ReviewWorklist> reviewWorkLists;

	@Transient
	private Integer remainingTat;

	public WorkListItem() {
		super();
	}

	public WorkListItem(long id, String accountNumber, String patientName, int los, int tat, int dollarAmount,
			String status, Boolean cnr, Boolean audited, int coderId, int clientId, String chartId,
			Date dischargedDate, Date admittedDate, Timestamp lastUpdateDate) {
		super();
		this.id = id;
		this.accountNumber = accountNumber;
		this.patientName = patientName;
		this.los = los;
		this.tat = tat;
		this.dollarAmount = dollarAmount;
		this.status = status;
		this.audited = audited;
		this.coderId = coderId;
		this.chartSpl = chartId;
		this.dischargedDate = dischargedDate;
		this.admittedDate = admittedDate;
		this.updatedDate = lastUpdateDate;
	}

	/**
	 * Constructor with mandatory elements
	 * 
	 * @param accountNumber
	 * @param patientName
	 * @param dischargedDate
	 * @param admittedDate
	 * @param los
	 * @param tat
	 * @param dollarAmount
	 * @param specialization
	 */
	public WorkListItem(String accountNumber, String patientName, Date dischargedDate, Date admittedDate, Integer los,
			Integer tat, Integer dollarAmount, String specialization) {

		this.accountNumber = accountNumber;
		this.patientName = patientName;
		this.dischargedDate = dischargedDate;
		this.admittedDate = admittedDate;
		this.los = los;
		this.tat = tat;
		this.dollarAmount = dollarAmount;
		this.updatedDate = new Timestamp(System.currentTimeMillis());

	}

	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getMrNumber() {
		return mrNumber;
	}

	public void setMrNumber(String mrNumber) {
		this.mrNumber = mrNumber;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public Integer getLos() {
		return los;
	}

	public void setLos(Integer los) {
		this.los = los;
	}

	public Integer getTat() {
		return tat;
	}

	public void setTat(Integer tat) {
		this.tat = tat;
	}

	public Integer getDollarAmount() {
		return dollarAmount;
	}

	public void setDollarAmount(Integer dollarAmount) {
		this.dollarAmount = dollarAmount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getAudited() {
		return audited;
	}

	public void setAudited(Boolean audited) {
		this.audited = audited;
	}

	public Integer getCoderId() {
		return coderId;
	}

	public void setCoderId(Integer coderId) {
		this.coderId = coderId;
	}

	public ClientDetails getClientDetails() {
		return clientDetails;
	}

	public void setClientDetails(ClientDetails clientDetails) {
		this.clientDetails = clientDetails;
	}

	public String getChartSpl() {
		return chartSpl;
	}

	public void setChartSpl(String chartSpl) {
		this.chartSpl = chartSpl;
	}

	public Date getDischargedDate() {
		return dischargedDate;
	}

	public void setDischargedDate(Date dischargedDate) {
		this.dischargedDate = dischargedDate;
	}

	public Date getAdmittedDate() {
		return admittedDate;
	}

	public void setAdmittedDate(Date admittedDate) {
		this.admittedDate = admittedDate;
	}

	public Timestamp getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Timestamp getAuditDate() {
		return auditDate;
	}

	public void setAuditDate(Timestamp auditDate) {
		this.auditDate = auditDate;
	}

	public double getEffortMetric() {
		return effortMetric;
	}

	public void setEffortMetric(double effortMetric) {
		this.effortMetric = effortMetric;
	}

	public Timestamp getUploadedDate() {
		return uploadedDate;
	}

	public void setUploadedDate(Timestamp uploadedDate) {
		this.uploadedDate = uploadedDate;
	}

	public String getInsurance() {
		return insurance;
	}

	public void setInsurance(String insurance) {
		this.insurance = insurance;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getCampusCode() {
		return campusCode;
	}

	public void setCampusCode(String campusCode) {
		this.campusCode = campusCode;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public Set<ReviewWorklist> getReviewWorkLists() {
		return reviewWorkLists;
	}

	public void setReviewWorkLists(Set<ReviewWorklist> reviewWorkLists) {
		this.reviewWorkLists = reviewWorkLists;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getCodedDate() {
		return codedDate;
	}

	public void setCodedDate(Date codedDate) {
		this.codedDate = codedDate;
	}

	public String getCoderName() {
		return coderName;
	}

	public void setCoderName(String coderName) {
		this.coderName = coderName;
	}

	public Integer getRemainingTat() {
		return remainingTat;
	}

	public void setRemainingTat(Integer remainingTat) {
		this.remainingTat = remainingTat;
	}

	public Boolean getIsUsedForAutoEMProcess() {
		return isUsedForAutoEMProcess;
	}

	public void setIsUsedForAutoEMProcess(Boolean isUsedForAutoEMProcess) {
		this.isUsedForAutoEMProcess = isUsedForAutoEMProcess;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public ReviewWorklist getReviewWorkListsByUser(final int userId) {
		if (reviewWorkLists != null) {
			for (ReviewWorklist reviewWorklist : reviewWorkLists) {
				if (reviewWorklist.getUserReviewd() == userId) {
					return reviewWorklist;
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param userId
	 * @return The ReviewWorklist object to get the details of last coding data
	 *         of the same worklist this is using to get the last coding history
	 *         of the worklist.
	 */
	public ReviewWorklist getWorklistPreviousCodingDetail(final int userId) {
		if (reviewWorkLists != null) {
			final List<ReviewWorklist> ReviewWorklistData = new ArrayList<>();

			for (ReviewWorklist reviewWorklist : reviewWorkLists) {
				if (reviewWorklist.getUserReviewd() != userId) {
					ReviewWorklistData.add(reviewWorklist);
				}
			}

			if (ReviewWorklistData.size() > 0) {
				Collections.sort(ReviewWorklistData, new ReviewWorklistComparator());
				return ReviewWorklistData.get(0);
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "WorkListItem [id=" + id + ", accountNumber=" + accountNumber + ", mrNumber=" + mrNumber
				+ ", patientName=" + patientName + ", los=" + los + ", tat=" + tat + "status=" + status + ", audited="
				+ audited + ", coderId=" + coderId + ", clientDetails=" + clientDetails.getId() + ", chartSpl="
				+ chartSpl + "]";
	}

}
