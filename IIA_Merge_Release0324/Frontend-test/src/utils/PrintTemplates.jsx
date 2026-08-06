import React from 'react';
import logo from '../assets/images/iia-logo.png'; // adjust path if your assets folder differs

// ── Status → human label ──
const QUALIFICATION_LABEL = {
  ACCEPTED: 'Qualified',
  Completed: 'Qualified',
  REJECTED: 'Not Qualified',
};
function qualificationLabel(raw) {
  if (!raw) return 'Pending';
  return QUALIFICATION_LABEL[raw] || raw;
}

function fmtDate(d) {
  if (!d) return '-';
  const dt = new Date(d);
  return isNaN(dt) ? String(d) : dt.toLocaleDateString('en-IN');
}
function fmtDateTime(d) {
  if (!d) return '-';
  const dt = new Date(d);
  return isNaN(dt) ? String(d) : dt.toLocaleString('en-IN');
}

function committeeMembersText({ evalStatus, dirCommitteeMembers }) {
  if (evalStatus?.committeeType === 'ADHOC') {
    return (
      (dirCommitteeMembers || [])
        .map((m) => `${m.memberName || m.name}${m.designation ? ` (${m.designation})` : ''}`)
        .join(', ') || 'NA'
    );
  }
  return (
    (evalStatus?.committeeVotes || [])
      .map((m) => `${m.committeeMemberName} — ${m.role || 'MEMBER'}`)
      .join(', ') || 'NA'
  );
}

function DocHeader() {
  return (
    <div className="doc-header">
      <img src={logo} alt="IIA logo" />
      <div className="org-name">INDIAN INSTITUTE OF ASTROPHYSICS</div>
      <div>2nd BLOCK, KORAMANGALA,</div>
      <div>BENGALURU – 560034</div>
    </div>
  );
}

function ProcurementDetailsTable({ formData, printDetails, tenderTitle, evalStatus, dirCommitteeMembers }) {
  return (
    <>
      <div className="section-banner">PROCUREMENT DETAILS</div>
      <table>
        <tbody>
          <tr>
            <th style={{ width: '20%' }}>INDENT NUMBER</th>
            <td style={{ width: '30%' }}>{formData.indentNumber || '-'}</td>
            <th style={{ width: '20%' }}>DATE</th>
            <td style={{ width: '30%' }}>{fmtDate(printDetails?.indentDate)}</td>
          </tr>
          <tr>
            <th>INDENTOR NAME</th>
            <td>{printDetails?.indentorName || '-'}</td>
            <th>DESIGNATION</th>
            <td>{printDetails?.designation || '-'}</td>
          </tr>
          <tr>
            <th>ENQUIRY/BID NO</th>
            <td>{printDetails?.enquiryBidNo || '-'}</td>
            <th>DATE</th>
            <td>{fmtDate(printDetails?.enquiryBidDate)}</td>
          </tr>
          <tr>
            <th>INDENT TYPE</th>
            <td>{printDetails?.indentType || '-'}</td>
            <th>INDENT VALUE (CCY)</th>
            <td>
              {formData.totalValue != null
                ? `₹ ${Number(formData.totalValue).toLocaleString('en-IN')}`
                : '-'}
            </td>
          </tr>
          <tr>
            <th>MODE</th>
            <td colSpan={3}>{formData.modeOfProcurement || '-'}</td>
          </tr>
          <tr>
            <th>TENDER TITLE</th>
            <td colSpan={3}>{tenderTitle || '-'}</td>
          </tr>
          <tr>
            <th>PROJECT NAME</th>
            <td colSpan={3}>{printDetails?.projectName || '-'}</td>
          </tr>
          <tr>
            <th>COMMITTEE MEMBERS</th>
            <td colSpan={3}>{committeeMembersText({ evalStatus, dirCommitteeMembers })}</td>
          </tr>
        </tbody>
      </table>
    </>
  );
}

function IntroductionBlock({ formData, evalStatus }) {
  return (
    <>
      <div className="section-banner">INTRODUCTION</div>
      <div className="free-text-box">{formData.introduction || evalStatus?.introduction || '-'}</div>
    </>
  );
}

function TechnicalSummaryTable({ quotationData, getQualificationStatus, getCombinedRemarks, docPhase }) {
  return (
    <>
      <div className="section-banner">TECHNICAL EVALUATION SUMMARY</div>
      <table>
        <thead>
          <tr>
            <th style={{ width: '8%' }}>Sl No</th>
            <th style={{ width: '30%' }}>Vendor Name</th>
            <th style={{ width: '27%' }}>Qualification Status</th>
            <th style={{ width: '35%' }}>Remarks</th>
          </tr>
        </thead>
        <tbody>
          {(quotationData || []).map((record, idx) => (
            <tr key={record.vendorId || idx}>
              <td>{idx + 1}</td>
              <td>{record.vendorName}</td>
              <td>{qualificationLabel(getQualificationStatus(record, docPhase))}</td>
              <td>{getCombinedRemarks(record, docPhase)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </>
  );
}

function TechnoCommercialSummaryTable({ quotationData }) {
  const ranked = (quotationData || [])
    .filter((q) => q.rank != null)
    .slice()
    .sort((a, b) => a.rank - b.rank);
  return (
    <>
      <div className="section-banner">TECHNO-COMMERCIAL EVALUATION SUMMARY</div>
      <table>
        <thead>
          <tr>
            <th style={{ width: '8%' }}>Sl No</th>
            <th style={{ width: '27%' }}>Vendor Name</th>
            <th style={{ width: '25%' }}>Total Offered Amount</th>
            <th style={{ width: '12%' }}>Rank</th>
            <th style={{ width: '28%' }}>Remarks</th>
          </tr>
        </thead>
        <tbody>
          {ranked.map((record, idx) => (
            <tr key={record.vendorId || idx}>
              <td>{idx + 1}</td>
              <td>{record.vendorName}</td>
              <td>
                {record.enteredAmount != null
                  ? `₹ ${Number(record.enteredAmount).toLocaleString('en-IN')}`
                  : '-'}
              </td>
              <td>{record.rank != null ? `L${record.rank}` : '-'}</td>
              <td>{record.spoRemarks || '-'}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </>
  );
}

function FreeTextSection({ title, text }) {
  return (
    <>
      <div className="section-banner">{title}</div>
      <div className="free-text-box">{text || '-'}</div>
    </>
  );
}

function DeclarationAndApproval({ printDetails }) {
  return (
    <>
      <div className="declaration-box">
        DECLARATION:
        <br />
        The members of the Committee hereby declare that they have examined the bids objectively
        and that none of them has any conflict of interest with any of the participating bidders.
      </div>
      <div className="approval-block">
        <div>
          <strong>Approved by the Director of the Institute</strong>
        </div>
        <div>Date &amp; Time of the Approval: {fmtDateTime(printDetails?.approvalDateTime)}</div>
        <div className="footnote">
          This is a system-generated document and does not require physical signatures.
        </div>
      </div>
    </>
  );
}

function TechnicalPrintDoc(props) {
  const {
    formData, evalStatus, quotationData, printDetails, tenderTitle, dirCommitteeMembers,
    getQualificationStatus, getCombinedRemarks,
  } = props;
  return (
    <div className="eval-doc">
      <DocHeader />
      <div className="doc-title">TECHNO-COMMERCIAL EVALUATION COMMITTEE'S RECOMMENDATIONS</div>
      <div className="section-banner">Technical Evaluation</div>
      <ProcurementDetailsTable
        formData={formData} printDetails={printDetails} tenderTitle={tenderTitle}
        evalStatus={evalStatus} dirCommitteeMembers={dirCommitteeMembers}
      />
      <IntroductionBlock formData={formData} evalStatus={evalStatus} />
      <TechnicalSummaryTable
        quotationData={quotationData} getQualificationStatus={getQualificationStatus}
        getCombinedRemarks={getCombinedRemarks} docPhase="TECHNICAL"
      />
      <FreeTextSection title="RECOMMENDATIONS" text={printDetails?.recommendations} />
      <DeclarationAndApproval printDetails={printDetails} />
    </div>
  );
}

function TechnoCommercialPrintDoc(props) {
  const {
    formData, evalStatus, quotationData, printDetails, tenderTitle, dirCommitteeMembers,
    getQualificationStatus, getCombinedRemarks,
  } = props;
  return (
    <div className="eval-doc">
      <DocHeader />
      <div className="doc-title">TECHNO-COMMERCIAL EVALUATION COMMITTEE'S RECOMMENDATIONS</div>
      <div className="section-banner">Techno-Commercial Evaluation</div>
      <ProcurementDetailsTable
        formData={formData} printDetails={printDetails} tenderTitle={tenderTitle}
        evalStatus={evalStatus} dirCommitteeMembers={dirCommitteeMembers}
      />
      <IntroductionBlock formData={formData} evalStatus={evalStatus} />
      <TechnicalSummaryTable
        quotationData={quotationData} getQualificationStatus={getQualificationStatus}
        getCombinedRemarks={getCombinedRemarks} docPhase="FINANCIAL"
      />
      <TechnoCommercialSummaryTable quotationData={quotationData} />
      <FreeTextSection
        title="FINANCIAL EVALUATION OBSERVATIONS AND RECOMMENDATIONS"
        text={printDetails?.financialObservations}
      />
      <FreeTextSection title="TERMS AND CONDITIONS" text={printDetails?.termsAndConditions} />
      <DeclarationAndApproval printDetails={printDetails} />
    </div>
  );
}

// ── Public entry — mount unconditionally at the bottom of TenderEvaluator,
//    hidden via CSS (.print-only) until activePrintDoc is set ──
export default function PrintTemplates({ docType, ...rest }) {
  return (
    <div className="print-only">
      {docType === 'TECHNICAL' && <TechnicalPrintDoc {...rest} />}
      {docType === 'TECHNO_COMMERCIAL' && <TechnoCommercialPrintDoc {...rest} />}
    </div>
  );
}