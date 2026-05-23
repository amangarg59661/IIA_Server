import React, { forwardRef, useEffect, useState } from "react";
import axios from "axios";
import logo from "../assets/images/iia-logo.png";

const PrintFormate = forwardRef(({ data }, ref) => {
 
  const getTotalValue = () =>
    data.materialDetails?.reduce(
      (sum, item) => sum + (Number(item.unitPrice || 0) * Number(item.quantity || 0)),
      0
    ) || 0;
  const [downloadLinks, setDownloadLinks] = useState({});
    
  const parseFileNames = (fileString) =>
    fileString ? fileString.split(",").map(f => f.trim()) : [];

  const fetchDownloadLinks = async (fileNames, type) => {
    const promises = fileNames.map(async (fileName) => {
      try {
        const response = await axios.get(`/file/download/Indent/${fileName}`, {
          responseType: 'blob'
        });

      const blob = response.data;
      if (blob.size === 0) {
        console.warn(`Blob is empty for file: ${fileName}`);
        return null;
      }

      // Get file extension from blob type
      const contentType = blob.type;
      let extension = "";
      if (contentType === "application/pdf") extension = ".pdf";
      else if (contentType.startsWith("image/")) extension = "." + contentType.split("/")[1];

      // Fix filename if it has wrong extension
      const correctedName = fileName.replace(/\.[^/.]+$/, extension);

      const downloadUrl = URL.createObjectURL(blob);
      return {
        name: correctedName,
        link: downloadUrl,
      };
    } catch (error) {
      console.error("Error fetching file:", fileName, error);
      return null;
    }
  });

  const results = await Promise.all(promises);
  return results.filter(Boolean);
};

/*

useEffect(() => {
  let isMounted = true;
  const loadLinks = async () => {
    const priorFiles = parseFileNames(data.priorApprovalsFileName);
    const techFiles = parseFileNames(data.technicalSpecificationsFile);

    const [priorLinks, techLinks] = await Promise.all([
      fetchDownloadLinks(priorFiles, "Prior"),
      fetchDownloadLinks(techFiles, "Tech"),
    ]);

    if (isMounted) {
      setDownloadLinks({
        uploadingPriorApprovals: Array.isArray(priorLinks) ? priorLinks : [],
        technicalSpecifications: Array.isArray(techLinks) ? techLinks : [],
      });
    }
  };

  loadLinks();

  return () => {
    isMounted = false;
  };
}, [data]);*/
  const renderDownloadLinks = (fileNames) => {
    if (!Array.isArray(fileNames) || fileNames.length === 0) {
      return <span>Not Attached</span>;
    }

    return (
      <div className="flex flex-col gap-1">
        {fileNames.map((fileName, index) => {
          const encodedFileName = encodeURIComponent(fileName.trim());
          const url = `/file/download/Indent/${encodedFileName}`;
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

  useEffect(() => {
    const priorFiles = parseFileNames(data.priorApprovalsFileName);
    const techFiles = parseFileNames(data.technicalSpecificationsFile);
    const draftFiles = parseFileNames(data.draftFileName);
    const buyBackFiles = parseFileNames(data.buyBackFileName);
    const pacAndBrandFiles = parseFileNames(data.pacAndBrandFileName)

    setDownloadLinks({
      uploadingPriorApprovals: priorFiles,
      technicalSpecifications: techFiles,
      uploadDraft: draftFiles,
      uploadBuyBack: buyBackFiles,
      uploadPacAndBrand: pacAndBrandFiles,
    });
  }, [data]);



  useEffect(() => {
    if (
      downloadLinks.uploadingPriorApprovals?.length ||
      downloadLinks.technicalSpecifications?.length ||
      downloadLinks.uploadDraft?.length ||
      downloadLinks.uploadBuyBack?.length ||
      downloadLinks.uploadPacAndBrand?.length
    ) {
      console.log("render download buttons", downloadLinks);
    }
  }, [downloadLinks]); 





  return (
    <>
      <style>
        {`
        @media print {
          @page {
            size: A4;
            margin: 10mm; 
          }

          html, body {
            margin: 0;
            padding: 0;
            height: 100%;
            width: 100%;
          }

          .a4-print-wrapper {
            width: 190mm; 
            margin: 0 auto;
            padding: 0;
            box-sizing: border-box;
          }

          .a4-print-page {
            box-sizing: border-box;
            border: 1px solid black; 
            padding: 10mm;
            background-color: white;
            min-height: 277mm; 
            page-break-after: always;
          }

          .page-break {
            page-break-before: always;
          }
        }

        `}
      </style>

      <div ref={ref} className="a4-print-wrapper text-[13px] font-sans text-black bg-white">
        <div className="a4-print-page">
         
          <div className="flex items-center justify-between mb-4">
            <img src={logo} alt="IIA Logo" className="w-20 h-20" />
            <div className="text-center flex-1 -ml-20">
              <h2 className="text-lg font-bold uppercase">INDIAN INSTITUTE OF ASTROPHYSICS</h2>
              <h2 className="text-lg font-bold uppercase">2nd BLOCK, KORAMANGALA,</h2>
              <h2 className="text-lg font-bold uppercase">BENGALURU – 560034</h2>
            </div>
          </div>

          <h4 className="font-bold text-center border border-black py-1 mb-2">INDENT</h4>

          
          <div className="mb-4">
            <h4 className="font-semibold text-center border border-black py-1 mb-2">INDENT DETAILS</h4>
            <table className="w-full border border-black border-collapse">
              <tbody>
                {twoColRow("INDENT NUMBER", data.indentId, "MODE OF PROCUREMENT", data.materialDetails?.[0]?.modeOfProcurement)}
                {twoColRow("INDENTOR NAME", data.indentorName, "VENDOR NAME(s)", data.materialDetails?.[0]?.vendorNames?.join(", "))}
                {twoColRow("MOBILE NO & EMAIL", `${data.indentorMobileNo} / ${data.indentorEmailAddress}`, "BUDGET CODE", data.materialDetails?.[0]?.budgetCode)}
                {twoColRow("DEPARTMENT", data.employeeDepartment, "PURPOSE", data.purpose)}
                {twoColRow("TYPE OF INDENT", "Material", "FY & QUARTER", data.quarter)}
                {twoColRow("PROJECT NAME", data.projectName, "BUYBACK", data.buyBack ? "Yes" : "No")}
                {twoColRow("CONSIGNEE", data.consignesLocation, "PRE-BID MEETING", data.preBidMeetingDate ? `${data.preBidMeetingDate}, ${data.preBidMeetingVenue}` : "N/A")}
                <tr>
                  <td className="border border-black p-2 font-semibold w-[30%]">JUSTIFICATION FOR PROPRIETARY</td>
                  <td className="border border-black p-2" colSpan={3}>{data.proprietaryAndLimitedDeclaration || "—"}</td>
                </tr>
              </tbody>
            </table>
          </div>

         
          <div className="mb-4">
            <h4 className="font-semibold text-center border border-black py-1 mb-2">MATERIAL / JOB DETAILS</h4>
            <table className="w-full border border-black border-collapse text-[12px]">
              <thead>
                <tr className="bg-gray-100">
                  {["Sl No", "Material/Job Code", "Material/Job Description", "Quantity", "UOM", "Unit Price (₹)", "Total Price (₹)"]
                    .map((heading, i) => (
                      <th key={i} className="border border-black px-2 py-1 text-left">{heading}</th>
                    ))}
                </tr>
              </thead>
              <tbody>
                {data.materialDetails?.map((item, i) => (
                  <tr key={i}>
                    <td className="border border-black px-2 py-1">{i + 1}</td>
                    <td className="border border-black px-2 py-1">{item.materialCode}</td>
                    <td className="border border-black px-2 py-1">{item.materialDescription}</td>
                    <td className="border border-black px-2 py-1">{item.quantity}</td>
                    <td className="border border-black px-2 py-1">{item.uom}</td>
                    <td className="border border-black px-2 py-1">{item.unitPrice}</td>
                    <td className="border border-black px-2 py-1">{(item.quantity * item.unitPrice).toFixed(2)}</td>
                  </tr>
                ))}
                <tr>
                  <td colSpan={6} className="border border-black px-2 py-1 text-right font-semibold">
                    Total Indent Value (₹)
                  </td>
                  <td className="border border-black px-2 py-1 font-semibold">{getTotalValue().toFixed(2)}</td>
                </tr>
              </tbody>
            </table>
          </div>

       <div className="mb-4">
        <h4 className="font-semibold text-center border border-black py-1 mb-2">DOCUMENTS</h4>
          <div  className="w-full border border-black p-4">
            <div className="mb-2">
              <p className="font-semibold">Prior Approvals (if any):</p>
                {renderDownloadLinks(downloadLinks.uploadingPriorApprovals)}
            </div>

            <div className="mb-2">
              <p className="font-semibold">Technical Specifications / Budgetary Quote:</p>
              {renderDownloadLinks(downloadLinks.technicalSpecifications)}
            </div>
            <div className="mb-2">
              <p className="font-semibold">Buyback Details:</p>
              {renderDownloadLinks(downloadLinks.uploadBuyBack)}
            </div>
            <div className="mb-2">
              <p className="font-semibold">Draft EOI/RFP:</p>
              {renderDownloadLinks(downloadLinks.uploadDraft)}
            </div>
            <div className="mb-2">
              <p className="font-semibold">PAC/BRAND PAC:</p>
              {renderDownloadLinks(downloadLinks.uploadPacAndBrand)}
            </div>
          </div>
        </div>
         
        <div className="mt-auto pt-6">
          <h4 className="font-semibold text-center border border-black py-1 mb-2">APPROVAL DETAILS & REMARKS</h4>
              <div className="w-full border border-black p-4">
                <p className="mb-2">
                  <strong>Approved By:</strong> {data.approvedBy || ""}
                </p>
                <p className="mb-2">
                  <strong>Date & Time:</strong> {data.date || ""}
                </p>
                <p>
                  <strong>Approver's / Recommender's Remarks:</strong> {data.remarks || ""}
                </p>
              </div>
          </div>
        </div>
      </div>
    </>
  );
});

const twoColRow = (label1, val1, label2, val2) => (
  <tr>
    <td className="border border-black p-2 font-semibold w-[30%] align-top">{label1}</td>
    <td className="border border-black p-2 w-[20%] align-top">
      {val1}
    </td>
    <td className="border border-black p-2 font-semibold w-[30%] align-top">{label2}</td>
    <td className="border border-black p-2 w-[20%] align-top">
      {val2}
    </td>
  </tr>
);






export default PrintFormate;

