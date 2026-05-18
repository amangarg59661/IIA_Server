import React, { forwardRef } from "react";
import logo from "../assets/images/iia-logo.png"; // Local logo as fallback

const PoFormat = forwardRef(({ po = {} }, ref) => {
  // Safely access nested poFormateData properties with optional chaining and defaults
  const data = po.poFormateData || {};
  const materialDetails = data.materialDetails || [];
  const poHistory = po.poHistory || [];

  return (
    <>
      <style>
        {`
          @page {
            size: A4;
            margin: 15mm;
          }
          *, *::before, *::after {
            box-sizing: border-box;
          }
          body {
            font-family: Arial, sans-serif;
            font-size: 12px;
            margin: 0;
            padding: 0;
            background: #fff;
          }
          .page {
            width: 190mm;           /* Nearly full A4 width with margins */
            min-height: 270mm;      /* Close to A4 height */
            padding: 15mm 10mm;     /* Balanced padding for layout */
            margin: 10mm auto;      /* Center horizontally with vertical spacing */
            border: 2px solid black;
            background: white;
            box-sizing: border-box;
            page-break-after: always; /* Ensures proper page breaks when printing */
          }
       .header-left {
  text-align: center;
  vertical-align: middle;
}

.logo-circle img {
  max-width: 80px;
  height: auto;
}

.header-middle {
  text-align: center;
  vertical-align: middle;
  font-size: 12px;
  line-height: 1.4;
}

.header-right {
  text-align: center;
  vertical-align: middle;
  font-weight: bold;
  font-size: 18px;
}

.header-po-title {
  font-size: 20px;
  font-weight: bold;
}

          .vendor-table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 15px;
            border: 1px solid black;
          }
          .vendor-table td {
            padding: 8px 10px;
            vertical-align: top;
            width: 50%;
            border: 1px solid black;
            font-size: 12px;
          }
          .two-col-table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 15px;
            border: 1px solid black;
            table-layout: fixed;
          }
          .two-col-table th,
          .two-col-table td {
            padding: 8px;
            border: none;
            border-bottom: 1px solid #aaa;
            vertical-align: top;
            font-size: 12px;
          }
          .two-col-table th {
            background: #f2f2f2;
            font-weight: bold;
            text-align: center;
            border-top: 1px solid black;
            border-bottom: 2px solid black;
          }
          .two-col-table th + th,
          .two-col-table td + td {
            border-left: 1px solid black !important;
          }
          .two-col-table tr:last-child td {
            border-bottom: 1px solid black;
          }
          .material-table {
            width: 100%;
            border-collapse: collapse;
            border: 1px solid black;
            margin-bottom: 15px;
            font-size: 12px;
            table-layout: fixed;
          }
          .material-table th,
          .material-table td {
            border: 1px solid black;
            padding: 8px 6px;
            text-align: center;
            word-wrap: break-word;
          }
          .material-table th {
            background: #f2f2f2;
            font-weight: bold;
          }
          .material-table tfoot td {
            font-weight: bold;
            text-align: right;
            padding-right: 10px;
          }
          .material-table tfoot td:last-child {
            text-align: center;
          }
         .po-history-table {
            width: 100%;
            border-collapse: collapse;
            border: 1px solid black;
            margin-bottom: 20px;
            font-size: 12px;
            table-layout: fixed;
          }
        .po-history-table th, .po-history-table td {
            border: 1px solid black;
            padding: 6px 8px;
            text-align: left;
            word-wrap: break-word;
          }
          .po-history-table th {
            background: #f2f2f2;
            font-weight: bold;
          }
          .terms-box {
            border: 1px solid black;
            padding: 10px 15px;
            font-size: 12px;
            margin-bottom: 20px;
          }
          .terms-bold {
            font-weight: bold;
            margin-bottom: 5px;
          }
        .signature-block {
  width: 100%;
  font-size: 12px;
  text-align: right;
  margin-top: 40px;
  padding-right: 0;
  line-height: 1.4;
}

          a {
            color: black;
            text-decoration: underline;
          }
          @media print {
            body,
            .page {
              margin: 0;
              box-shadow: none;
              width: auto;
              min-height: auto;
              padding: 0;
            }
          }
        `}
      </style>

      <div className="page" ref={ref} role="main" aria-label="Purchase order document">
        {/* Header Table */}
      <table className="header-table" border="1" width="100%">
  <tbody>
    <tr>
      {/* Left - Logo */}
      <td className="header-left" width="20%">
        <div className="logo-circle">
          <img src={logo} alt="IIA Logo" />
        </div>
      </td>

      {/* Middle - Institute Info */}
      <td className="header-middle" width="55%">
        <div className="header-info">
          <strong>भारतीय खगोल भौतिकी संस्थान</strong>
          <br />
          <strong>INDIAN INSTITUTE OF ASTROPHYSICS</strong>
          <br />
          2nd Block, Koramangala, Bengaluru, Karnataka, India
          <br />
          Pin–560034
          <br />
          PAN based GSTIN: 29AABTI0567R1ZZ
        </div>
      </td>

      {/* Right - Title */}
      <td className="header-right" width="25%">
        <div className="header-po-title">
          <div>क्रय आदेश</div>
          <div>PURCHASE ORDER</div>
        </div>
      </td>
    </tr>
  </tbody>
</table>


        {/* Vendor and PO Details */}
        <table className="vendor-table" role="table" aria-label="Vendor and Purchase Order details">
          <tbody>
            <tr>
              <td>
                Vendor Code: {data.vendorCode || ""}
                <br />
                Vendor Name: {data.vendorName || ""}
                <br />
                Address: {data.vendorAddress || ""}
                <br />
                GSTIN: {data.gstin || ""}
                <br />
                Contact: {data.contactNumber || ""}
                <br />
                Email: {data.email || ""}
                <br />
                Quotation No / Date: {data.quotationNo || ""} / {data.quotationDate || ""}
                <br />
              </td>
              <td>
                PO No / Date: {data.poNumber || ""} / {data.poDate || ""}
                <br />
                Indent No / Date: {data.indentIds || ""} / {data.indentDates || ""}
                <br />
                Tender No / Date: {data.tenderNumber || ""} / {data.tenderDate || ""}
                <br />
                Project: {data.projectName || ""}
                <br />
                Budget Code: {data.budgetCode || ""}
              </td>
            </tr>
          </tbody>
        </table>

        {/* Billing and Consignee Details */}
        <table
          className="two-col-table"
          style={{ tableLayout: "fixed", width: "100%" }}
          role="table"
          aria-label="Billing and Consignee Details"
        >
          <thead>
            <tr>
              <th>Bill To</th>
              <th>Consignee (Delivery to)</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>
                Indian Institute of Astrophysics
                <br />
                2nd Block, Koramangala,
                <br />
                Bengaluru, Karnataka, India
                <br />
                Pin– 560034
              </td>
              <td>{data.consigneeLocation || ""}</td>
            </tr>
          </tbody>
        </table>

        {/* Material Details Table */}
        <table className="material-table" role="table" aria-label="Material Details">
          <thead>
            <tr>
              <th>S. No.</th>
              <th>Material Description</th>
              <th>Quantity</th>
              <th>UOM</th>
              <th>Unit Price ({(materialDetails[0] && materialDetails[0].currency) || ""})</th>
              <th>GST Rate</th>
              <th>Duties</th>
              <th>Frieght Charges</th>
              <th>Total Price ({(materialDetails[0] && materialDetails[0].currency) || ""})</th>
            </tr>
          </thead>
          <tbody>
            {materialDetails.map((mat, index) => (
              <tr key={index}>
                <td>{index + 1}</td>
                <td>{mat.materialDescription}</td>
                <td>{mat.quantity}</td>
                <td>{mat.uom}</td>
                <td>{mat.unitPrice}</td>
                <td>{mat.gstRate}</td>
                <td>{mat.duties}</td>
                <td> {mat.freightCharge}</td>
                <td>{mat.totalMaterialPrice}</td>
              </tr>
            ))}
          </tbody>
          <tfoot>
            <tr>
              <td colSpan="8" style={{ textAlign: "right" }}>
                Total
              </td>
              <td>{data.totalAmount || ""}</td>
            </tr>
            <tr>
              <td colSpan="8" style={{ textAlign: "right" }}>
                (+) GST Amount
              </td>
              <td>{data.totalGst || ""}</td>
            </tr>
            <tr>
              <td colSpan="8" style={{ textAlign: "right" }}>
                (-) Buyback Amount
              </td>
              <td>{data.buyBackAmount || ""}</td>
            </tr>
            <tr>
              <td colSpan="8" style={{ textAlign: "right" }}>
                Grand Total
              </td>
              <td>{data.grandTotal || ""}</td>
            </tr>
          </tfoot>
        </table>
        {/* New PO History Table */}
        <table className="po-history-table" role="table" aria-label="Purchase Order History">
          <thead>
            <tr>
              <th>Stage</th>
              <th>Action</th>
              <th>Remarks</th>
              <th>Date</th>
            </tr>
          </thead>
           <tbody>
            {poHistory.length > 0 ? (
              poHistory.map((history, idx) => (
                <tr key={idx}>
                  <td>{history.status || "-"}</td>
                 
                  <td>{history.action || "-"}</td>
                  <td>{history.remarks || "-"}</td>
                
                  <td>{history.createdDate ? new Date(history.createdDate).toLocaleDateString() : "-"}</td>
                </tr>
                 ))
            ) : (
              <tr>
                <td colSpan="6" style={{ textAlign: "center" }}>
                  No purchase order history available
                </td>
              </tr>
            )}
          </tbody>
        </table>

        {/* Terms and Conditions */}
        <div className="terms-box" role="region" aria-label="Terms and Conditions">
          <div className="terms-bold">Terms & Conditions:</div>
          Warranty: {data.warranty || ""}
          <br />
          Inco Term: {data.incoTerms || ""}
          <br />
          Payment Term: {data.paymentTerms || ""}
          <br />
          Duties & Taxes: {data.duties || ""}
          <br />
          Delivery Period: {data.deliveryPeriod || ""} weeks from date of PO.
          <br />
          Liquidated Damages: @ 0.5% per week to a maximum of 10% of PO value will be deducted for delay in delivery.
          <br />
          Performance & Warranty Security:{" "}
  {data.performanceAndWarrantySecurity && data.performanceAndWarranty ? (
    <span>
      CCY Amount ({data.currencyOfMaterial} {data.performanceAndWarrantySecurity}) (computed as per percentage {data.performanceAndWarranty} mentioned in PO) to be submitted within 14 days from the date of purchase order as Security towards performance and warranty whose validity shall be 60 days beyond warranty/contract period.
    </span>
  ) : (
    <span>-</span>
  )}
          <br />
          Freight Forwarder: {data.freightForwarderDetails || ""}
          <br />
          Additional Terms And Conditions (ATC): {data.additionalTermsAndConditions || ""}
          <br />
          General Terms & Conditions: ( link to Download the doc )
        </div>

        {/* Additional information and Signature */}
        <p>
          Please confirm your acceptance to fulfil the purchase order under specified terms. If no acceptance is received within seven days from the date
          of receipt of this order, it will be deemed that this order and its terms has been accepted.
        </p>
        <p>
          Mark any communications to <a href="mailto:purchase@iiap.res.in">purchase@iiap.res.in</a>
        </p>
        <p>For Purchase related queries contact: 080 2254 1384/ 1363/ 1244 </p>
        <p>For Delivery and Payment related queries contact: 080 2254 1340/ 1234</p>

     <div
  className="signature-block"
  style={{
    display: "flex",
    flexDirection: "column",
    alignItems: "flex-end", // Push everything to the right
    marginTop: "40px",
    fontSize: "12px",
    lineHeight: "1.4",
    paddingRight: "20px", // Added padding to the right
  }}
  role="contentinfo"
>
  <div>भवदीय Yours faithfully,</div> <br/>

  {data.officerSignatureBase64 && (
    <img
      src={`data:image/png;base64,${data.officerSignatureBase64}`}
      alt="Officer Signature"
      style={{ width: "150px", height: "auto", marginTop: "5px" }}
    />
  )}<br/>

  <div>भंडार एवं क्रय अधिकारी Stores Purchase Officer</div>
  <div>कृते निदेशक For And on behalf of Director</div>
</div>


        <p>This is a system generated document and hence ink Signature is not required.</p>
      </div>
    </>
  );
});

export default PoFormat;
