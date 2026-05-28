import React, { forwardRef } from "react";
//import logo from "../assets/images/iia-logo.png";
import MyLogo from "../assets/iia-logo.png";

const TenderPrintFormat = forwardRef(({ data = {} }, ref) => {
  const materials = data.materialDetails || [];
  const parseFileNames = (fileString) =>
    fileString ? fileString.split(",").map(f => f.trim()) : [];

   const renderDownloadLinks = (fileNames) => {
    if (!Array.isArray(fileNames) || fileNames.length === 0) {
      return <span>Not Attached</span>;
    }

    return (
      <div className="flex flex-col gap-1">
        {fileNames.map((fileName, index) => {
          const encodedFileName = encodeURIComponent(fileName.trim());
          const url = `http://localhost:8081/astro-service/file/download/Tender/${encodedFileName}`;
          return (
            <a
              key={index}
              href={url}
              target="_blank"
              rel="noopener noreferrer"
              className="text-blue-600 underline break-all"
            >
              {url}
            </a>
          );
        })}
      </div>
    );
  };
 /* const renderDownloadLinks = (fileNames) => {
  if (!Array.isArray(fileNames) || fileNames.length === 0) {
    return <span className="italic text-gray-500">Not Attached</span>;
  }

  return (
    <div className="flex flex-col items-end gap-1">
      {fileNames.map((fileName, index) => {
        const encoded = encodeURIComponent(fileName);
        const url = `http://localhost:8081/astro-service/file/download/Tender/${encoded}`;
         console.log("Download URL:", url); 
        return (
         <a
            key={index}
            href={url}
            target="_blank"
            rel="noopener noreferrer"
            className="italic text-blue-600 underline text-[13px]"
          >
            (url)
          </a>
        );
      })}
    </div>
  );
};*/


  return (
    <div
      ref={ref}
      className="w-[210mm] min-h-[297mm] px-[25mm] py-[20mm] text-black text-[13px] font-sans print:p-[25mm] print:overflow-visible"
    >
      {/* Header: Logo left, text centered */}
      <div className="flex justify-between items-start mb-4">
        <img src={MyLogo} alt="IIA Logo" className="h-14" />
        <div className="text-center flex-1 -ml-14">
          <div><strong>Indian Institute of Astrophysics</strong></div>
          <div><strong>2nd Block, Koramangala, Bangalore – 560034</strong></div>
          <div>
            <strong>
              Ph: 080-22541244, Email:{" "}
              <span className="text-blue-600 underline">purchase@iiap.res.in</span>
            </strong>
          </div>
          <div><strong>Website: www.iiap.res.in</strong></div>
        </div>
      </div>

      {/* Title */}
      <div className="text-center text-[16px] font-bold underline mb-4">TENDER</div>

      {/* Tender Info */}
      <div className="flex justify-between mb-2">
        <div><strong>Tender Enquiry No.:</strong> {data.tenderId || "__________"}</div>
        <div><strong>Dated:</strong> {data.tenderDate || "__________"}</div>
      </div>

      {/* Vendor */}
      <div className="mb-4">
        <strong>M/s {data.vendorName}</strong><br />
        {data.vendorAddress1 || "____________________"}<br />
        {data.vendorAddress2 || "____________________"}
      </div>

      {/* Tender Details Table */}
      <table className="w-full table-fixed border border-black border-collapse mb-5">
  <thead>
    <tr>
     <th colSpan={2} className="border border-black px-2 py-1 text-center font-bold">
        Tender Details
      </th>

    </tr>
  </thead>
  <tbody className="[&>tr>td]:px-2 [&>tr>td]:py-1">
    {[
      ["Tender Inviting Authority", "Indian Institute of Astrophysics"],
      ["Tender Title", data.titleOfTender || ""],
      ["Tender Start Date", data.openingDate || ""],
      ["Pre-bid Meeting Time, Date & Place", data.preBidMeeting || ""],
      ["Tender End Date", data.closingDate || ""],
      ["Tender Opening Date", data.openingDate || ""],
      ["Tender Offer Validity (From Tender End Date)",
        (() => {
          const parseDate = (str) => {
            if (!str) return null;
              const [day, month, year] = str.split("/");
              return new Date(year, month - 1, day);
             };

            const start = parseDate(data.openingDate);
            const end = parseDate(data.closingDate);
          if (start && end && !isNaN(start) && !isNaN(end)) {
            const diffDays = Math.floor((end - start) / (1000 * 60 * 60 * 24));
           return `${diffDays} Days`;
          }
          return "____ Days";
        })()
      ],["Buyer Email", "purchase@iiap.res.in"],
      ["Type of Tender", data.bidType === "Double" ? "Two Bid System" : "Single Bid System"],
      ["Time allowed for technical clarifications", "2 Days"],
      ["Estimated Tender Value (₹)", `₹ ${data.totalTenderValue || "Indent Total Value"}`],
      ["Evaluation Method", data.evaluationMethod || "1) Item value wise evaluation (or) 2) Total value wise evaluation"],
      ["Consignee and Address", data.consignes || ""],
    ].map(([label, value], idx) => {
      const shouldBoldRight =
        value === "Indian Institute of Astrophysics" ||
        value === "purchase@iiap.res.in";
      const isEmail = value === "purchase@iiap.res.in";

      return (
        <tr key={idx}>
          <td className="border border-black w-1/2">
            <strong>{label}</strong>
          </td>
          <td className="border border-black">
            {shouldBoldRight ? (
              <strong className={isEmail ? "text-blue-600 underline" : ""}>{value}</strong>
            ) : (
              value
            )}
          </td>
        </tr>
      );
    })}

    {/* Special Instructions */}
    <tr>
      <td className="border border-black">
        <strong>Special Instructions</strong>
      </td>
      <td className="border border-black">
        <ol className="list-decimal ml-4 space-y-1">
          <li>{data.instruction1 || ""}</li>
          <li>{data.instruction2 || ""}</li>
          <li>{data.instruction3 || ""}</li>
        </ol>
      </td>
    </tr>
  </tbody>
</table>




      {/* Materials Table (narrower & centered) */}
      <div className="text-[14px] font-semibold underline mb-1 text-center">
        LIST OF MATERIALS / JOB
      </div>
      <div className="w-full flex justify-center mb-5 print:pt-[20mm]">
        <table className="w-[85%] table-fixed border border-black border-collapse break-inside-avoid">
          <thead>
            <tr className="bg-gray-100 text-left">
              <th className="border border-black px-2 py-1 w-[8%]">Sl No</th>
              <th className="border border-black px-2 py-1">Material / Job Description</th>
              <th className="border border-black px-2 py-1 w-[15%]">Quantity</th>
              <th className="border border-black px-2 py-1 w-[12%]">UOM</th>
            </tr>
          </thead>
          <tbody>
            {(materials.length > 0 ? materials : Array(3).fill({})).map((item, index) => (
              <tr key={index}>
                <td className="border border-black px-2 py-1">{index + 1}</td>
                <td className="border border-black px-2 py-1">{item.materialDescription || ""}</td>
                <td className="border border-black px-2 py-1">{item.quantity || ""}</td>
                <td className="border border-black px-2 py-1">{item.uom || ""}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
            {/* DOCUMENTS Section */}

{/* Heading - OUTSIDE the border */}
<div className="text-center font-semibold mt-8 mb-2">
  DOCUMENTS
</div>

{/* Document List - INSIDE bordered box */}
<div className="border border-black p-4 text-[13px]">
  <div className="space-y-2">
    {[
      ["1.", "Technical Specifications/ RFP", "multiple docs & if applicable", data.uploadTenderDocumentsFileName],
      ["2.", "Bid Security Declaration Format", "if applicable", data.bidSecurityDeclarationFile],
      ["3.", "Buyback Details", "if applicable", data.uploadBuyBackDetails], // Optional
      ["4.", "MII Local Content Declaration Format", "if applicable", data.miiStatusDeclarationFileName],
      ["5.", "Country of Origin Format", "", data.uploadCountryOfOrigin], // Optional
      ["6.", "General Terms & Conditions", "", data.uploadGeneralTermsAndConditionsFileName],
    ].map(([no, title, note, files], i) => (
      <div key={i} className="flex justify-between items-start">
        <div>
          <strong>{no} {title}</strong>
          {note && <> <em>({note})</em></>}
        </div>
        <div> {renderDownloadLinks(parseFileNames(files))}</div>
      </div>
    ))}
  </div>
</div>



{/* Note, Signature & Footer - OUTSIDE border */}
<div className="mt-4 text-[13px]">
 { /*<p className="font-semibold mb-4">
    <strong>Note:</strong> 1. THE TERMS AND CONDITIONS AS IN THE OVERLEAF OF THIS ENQUIRY MUST BE COMPLIED WITH CAREFULLY.
  </p>*/}

 {/* Signature Block - aligned and padded */}
<div className="flex justify-end mt-8 text-[13px] pr-10">
  <div className="text-right">
    <p className="text-center font-semibold">Regards,</p>
    <p className="text-center font-semibold my-3">Sd/-</p>
    <p className="text-center font-semibold pr-2">Sr. Stores & Purchase Officer</p>
  </div>
</div>


  <p className="italic text-[12px]">
    *This is a system generated document and hence Signature is not required
  </p>
</div>

     
   </div>
  );
});

export default TenderPrintFormat;
