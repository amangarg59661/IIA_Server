import { Card, message } from "antd";
import React, { useEffect, useRef, useState } from "react";
import Heading from "../../../components/DKG_Heading";
import CustomForm from "../../../components/DKG_CustomForm";
import { renderFormFields } from "../../../utils/CommonFunctions";
import ButtonContainer from "../../../components/ButtonContainer";
import { useReactToPrint } from "react-to-print";
import axios from "axios";
import { useSelector } from "react-redux";
import CustomModal from "../../../components/CustomModal";
import { useLocation } from "react-router-dom";
import GrnSearchDropdown from "../../../components/GrnSearchDropDown";

const Grn = () => {
  const printRef = useRef();
  const handlePrint = useReactToPrint({
    content: () => printRef.current,
  });

  const locatorMaster = useSelector(state => state.masters.locatorMaster)

  const [modalOpen, setModalOpen] = useState(false);
  const [submitBtnLoading, setSubmitBtnLoading] = useState(false);
  const [isDepreciationDisabled, setIsDepreciationDisabled] = useState(false);
  const location = useLocation();
  const processNoFromState = location.state?.processNo;

  const [formData, setFormData] = useState({
    giNo: "",
    materialDtlList: [],
    grnType: "GI",
  });
  const handleChange = (fieldName, value) => {
    if (typeof fieldName === "string") {
      setFormData((prev) => ({ ...prev, [fieldName]: value }));
    } else {
      setFormData((prev) => {
        const prevMaterialDtlList = [...prev.materialDtlList];
        prevMaterialDtlList[fieldName[1]] = {
          ...prevMaterialDtlList[fieldName[1]],
        };
        prevMaterialDtlList[fieldName[1]][fieldName[2]] = value;

        if (fieldName[2] === "depriciationRate") {
          const material = prevMaterialDtlList[fieldName[1]];
          const unitPrice = parseFloat(material.unitPrice || 0);
          const acceptedQuantity = parseFloat(material.acceptedQuantity || 0);
          const depriciationRate = parseFloat(value || 0);

          // const purchaseDateStr =pre.deliveryDate;
          const purchaseDateStr =
            prev.gprnDtls?.deliveryDate || prev.deliveryDate;

          let yearsPassed = 0;

          if (purchaseDateStr) {
            const [day, month, year] = purchaseDateStr.split("/");
            const purchaseDate = new Date(`${year}-${month}-${day}`);
            const today = new Date();

            yearsPassed = today.getFullYear() - purchaseDate.getFullYear();
            const monthDiff = today.getMonth() - purchaseDate.getMonth();
            if (
              monthDiff < 0 ||
              (monthDiff === 0 && today.getDate() < purchaseDate.getDate())
            ) {
              yearsPassed--;
            }
            if (yearsPassed < 0) yearsPassed = 0;
          }

          const purchaseValue = unitPrice * acceptedQuantity;
          const bookValue =
            yearsPassed > 0
              ? purchaseValue *
                Math.pow(1 - depriciationRate / 100, yearsPassed)
              : purchaseValue;

          prevMaterialDtlList[fieldName[1]].bookValue = bookValue.toFixed(2);
        }

        return { ...prev, materialDtlList: prevMaterialDtlList };
      });
    }
  };

  console.log("Foprmdata: ", formData);

  const handleSearch = async (selectedGiNo) => {
    let giNo = selectedGiNo || formData.giNo;
    if(formData.grnType === "materialIn") {
      try{

        const {data} = await axios.get(`/api/process-controller/getIgpMaterialDtls?igpId=${formData.giNo}`)
        setFormData({...formData, ...data.responseData, indentorName: data.responseData.indentId, custodianName: data.responseData.custodianName,materialDtlList: data.responseData.materialDtlList.map(item => ({...item, materialDesc: item.description, uomId: item.uom, unitPrice: item.estimatedPriceWithCcy}))})
        console.log("DATA: ", data.responseData);
      }
      catch(error){
        console.log("ERROR", error);
      }
      return;
    }
    try {
      let processStage = "GRN";
      let processNo = formData.grnNo;

      /* // Override stage and number if GRN number is present
    if (formData.grnType == "GI") {
      processStage = "GI";
      processNo = formData.giNo;
    } else if (formData.grnType === "IGP") {
      processStage = "IGP";
      processNo = formData.giNo;
    }*/
      if (!formData.grnNo) {
        if (formData.grnType === "GI") {
          processStage = "GI";
        //  processNo = formData.giNo;
         processNo = selectedGiNo;
        } else if (formData.grnType === "IGP") {
          processStage = "IGP";
          processNo = formData.giNo;
        }
      }

      const { data } = await axios.get(
        `/api/process-controller/getSubProcessDtls?processStage=${processStage}&processNo=${processNo}`
      );

      if (processStage === "GI") {
        const deliveryDate = data?.responseData?.gprnDtls?.deliveryDate;

        // Prepare delivery date for calculation
        let isDepreciationDisabled = false;
        if (deliveryDate) {
          const [day, month, year] = deliveryDate.split("/");
          const poDate = new Date(`${year}-${month}-${day}`);
          const today = new Date();
          const oneYearLater = new Date(poDate);
          oneYearLater.setFullYear(oneYearLater.getFullYear() + 1);
          isDepreciationDisabled = today < oneYearLater;
        }

        const materialWithPrice =
          data?.responseData?.giDtls?.materialDtlList?.map((material) => {
            const gprnMaterial =
              data?.responseData?.gprnDtls?.materialDtlList?.find(
                (m) => m.materialCode === material.materialCode
              );
            const unitPrice = parseFloat(gprnMaterial?.unitPrice || 0);
            const acceptedQuantity = parseFloat(material.acceptedQuantity || 0);

            let bookValue = 0;
            let depriciationRate = material.depriciationRate || 0;

            if (isDepreciationDisabled) {
              bookValue = acceptedQuantity * unitPrice;
              depriciationRate = 0; // hide/disable input
            } else {
              // initial value until user inputs depreciationRate
              bookValue =
                acceptedQuantity * unitPrice * (1 - depriciationRate / 100);
            }

            return {
              ...material,
              unitPrice,
              bookValue: parseFloat(bookValue.toFixed(2)),
              depriciationRate,
            };
          });

        setFormData({
          ...data?.responseData?.giDtls,
          indentorName: data?.responseData?.gprnDtls?.receivedBy,
          giNo: data?.responseData?.giDtls?.inspectionNo,
          grnType: "GI",
          materialDtlList: materialWithPrice,
          locationId: data?.responseData?.gprnDtls?.locationId,
          custodianId: data?.responseData?.gprnDtls?.receivedBy,
          custodianName: data?.responseData?.gprnDtls?.receivedName,
          //deliveryDate,
          gprnDtls: {
             ...data?.responseData?.gprnDtls,
            deliveryDate,
          },
        });

        setIsDepreciationDisabled(isDepreciationDisabled);
      } else if (processStage === "IGP") {
        setFormData({
          ...data?.responseData,
          giNo: data?.responseData?.igpId,
          grnType: "IGP",
          materialDtlList: data?.responseData?.materialDtlList?.map(
            (material) => ({
              ...material,
              acceptedQuantity: material.quantity,
            })
          ),
        });
      } else if (processStage === "GRN") {
        setFormData({
          ...data?.responseData,
          giNo: data?.responseData?.grnDtls?.giNo,
          grnSubProcessId: data?.responseData?.grnDtls?.grnSubProcessId,
          grnType: "GI",
          grnNo: data?.responseData?.grnDtls?.grnNo,
          grnDate: data?.responseData?.grnDtls?.grnDate,
          installationDate: data?.responseData?.grnDtls?.installationDate,
          commissioningDate: data?.responseData?.grnDtls?.commissioningDate,
          indentorName: data?.responseData.grnDtls?.custodianId,
          gprnAmount: data?.responseData.gprnDtls.gprnAmount,
          poAmount: data?.responseData.gprnDtls.poAmount,
          materialDtlList: data?.responseData?.grnDtls?.materialDtlList?.map(
            (grnMaterial) => {
              const giMaterial =
                data?.responseData?.giDtls?.materialDtlList?.find(
                  (m) => m.assetId === grnMaterial.assetId
                ) || {};

              const gprnMaterial =
                data?.responseData?.gprnDtls?.materialDtlList?.find(
                  (m) => m.materialCode === giMaterial.materialCode
                ) || {};

              const unitPrice = parseFloat(gprnMaterial?.unitPrice || 0);
              const acceptedQuantity = grnMaterial.quantity || 0;
              const depriciationRate = grnMaterial.depriciationRate || 0;

              let bookValue = 0;
              if (isDepreciationDisabled) {
                bookValue = acceptedQuantity * unitPrice;
              } else {
                bookValue =
                  acceptedQuantity * unitPrice * (1 - depriciationRate / 100);
              }

              return {
                ...grnMaterial,
                //   materialCode: giMaterial.materialCode || "",
                //   materialDesc: giMaterial.materialDesc || "",
                assetDesc: giMaterial.assetDesc || "",
                uomId: giMaterial.uomId || "",
                receivedQuantity: giMaterial.receivedQuantity || 0,
                unitPrice,
                acceptedQuantity,
                locatorId: grnMaterial.locatorId || 0,
                depriciationRate,
                bookValue: parseFloat(bookValue.toFixed(2)),
              };
            }
          ),
        });
      }

      /*else if (processStage === "GRN") {
      console.log("Handling GRN Type:", data?.responseData?.grnDtls);

      setFormData({
    ...data?.responseData,
    giNo: data?.responseData?.grnDtls?.giNo,  
    grnType: "GI",  
    grnNo: data?.responseData?.grnDtls?.grnNo, 
    grnDate: data?.responseData?.grnDtls?.grnDate, 
    installationDate: data?.responseData?.grnDtls?.installationDate,  
    commissioningDate: data?.responseData?.grnDtls?.commissioningDate,  
    indentorName:data?.responseData.grnDtls?.createdBy,
    materialDtlList: data?.responseData?.grnDtls?.materialDtlList?.map(material => ({
      ...material,
      acceptedQuantity: material.quantity || 0, 
      locatorId: material.locatorId || 0,  
      depriciationRate: material.depriciationRate || 0, 
      bookValue: material.bookValue || 0,  
    })),
      });
    }*/
    } catch (error) {
      message.error(
        error?.response?.data?.responseStatus?.message || "Error fetching data."
      );
    }
  };


  const { userId } = useSelector((state) => state.auth);

  const onFinish = async () => {
    const payload = { ...formData, createdBy: userId };

    if(formData.grnType === "materialIn"){
      try{
        const {data} = await axios.post("/api/process-controller/saveMaterialGrn", payload)
        message.success("Material GRN saved successfully.");
        setFormData(prev => ({...prev, grnNo: data.responseData.processNo}))
      }
      catch(error){
        console.error("Error: ", error)
        message.error(error?.response?.data?.responseStatus?.message || "Error saving Material GRN")
      }
      return;
    }

    try {
      setSubmitBtnLoading(true);
      // const {data} = await axios.post("/api/process-controller/saveGrn", payload);
      const isUpdate = !!formData.grnNo;
      const apiUrl = isUpdate
        ? "/api/process-controller/updateGrn"
        : "/api/process-controller/saveGrn";

      const { data } = await axios.post(apiUrl, payload);

      setFormData((prev) => ({
        ...prev,
        grnNo: data?.responseData?.processNo,
        acceptedQuantity: data?.responseData?.acceptedQuantity,
      }));

      localStorage.removeItem("grnDraft");
      // setModalOpen(true);
      if (isUpdate) {
        message.success("Updated Successfully.");
      } else {
        setModalOpen(true);
      }
    } catch (error) {
      message.error(
        error?.response?.data?.responseStatus?.message || "Failed to save GRN."
      );
    } finally {
      setSubmitBtnLoading(false);
    }
  };

  useEffect(() => {
    const draft = localStorage.getItem("grnDraft");
    if (draft) {
      setFormData(JSON.parse(draft));
      message.success("Form loaded from draft.");
    }
  }, []);
  useEffect(() => {
    if (processNoFromState) {
      setFormData((prev) => ({ ...prev, grnNo: processNoFromState }));
    }
  }, [processNoFromState]);
  useEffect(() => {
    if (formData?.grnNo) {
      handleSearch();
    }
  }, [formData?.grnNo]);

  const [ldd, setLdd] = useState([])

  const handleLocatorDropdown = () => {
    if(!formData.locationId) {
      setLdd([])
    }
    else{
      const locatorList = locatorMaster.filter(loc => loc.locationId === formData.locationId)
      setLdd(locatorList)
    }
  }


  useEffect(() => {
    handleLocatorDropdown()
  }, [formData.locationId])





// const locatorMaster = [
//     {
//         value: "1",
//         label: "Locator 1"
//     },
//     {
//         value: "2",
//         label: "Locator 2"
//     },
//     {
//         value: "3",
//         label: "Locator 3"
//     },
//     {
//         value: "4",
//         label: "Locator 4"
//     },
// ]

const grvFields =(formData)=> [
    {
        heading: "Order Details",
        colCnt: 5,
        fieldList: [
            {
                name: "grnType",
                label: "GRN Type",
                type: "select",
                required: true,
                options: [
                    {
                        value: "GI",
                        label: "GI"
                    },
                    // {
                    //     value: "IGP",
                    //     label: "IGP"
                    // },
                    {
                        value: "materialIn",
                        label: "Material Inward"
                    }
                ],
            },
           /* {
                name: "giNo",
                label: "Enter Process No",
                type: "search",
                required: true,
                span: 2
            }*/
        {
  name: "giNo",
  label: "Enter GI No",
  type: "custom",
  render: () => (
   <GrnSearchDropdown
  label="GI No"
  value={formData.giNo}
  onChange={(val) => setFormData(prev => ({ ...prev, giNo: val }))}
  onSelect={(selectedItem) => {
    if (selectedItem) {
      const selectedGiNo = selectedItem.giNo;

      // Update state
      setFormData(prev => ({ ...prev, giNo: selectedGiNo }));

      // Call search immediately with selected value
      handleSearch(selectedGiNo);
    }
  }}
/>


  ),
  required: true,
  span: 2
},
            {
                name: "grnNo",
                label: "GRN No",
                type: "search",
              //  disabled: true,
                span: 2,
            },
            {
                name: "grnDate",
                label: "GRN Date",
                type: "date",
                required: true
            },
            {
                name: "installationDate",
                label: "Installation Date",
                type: "date",
              //  required: true
            },
            {
                name: "commissioningDate",
                label: "Commission Date",
                type: "date",
              //  required: true
            },
             {
              name: "gprnAmount",
              label: "grn Amount",
              type: "text",
              span: 2,
            },
             {
              name: "poAmount",
              label: "Po Amount",
              type: "text",
              span: 2,
            },
         ...(formData?.grnType === "GI" &&
    formData?.gprnDtls?.materialDtlList?.some(m => m.category === "Consumable")
    ? [
        {
          name: "storesStock",
          label: "Stores Stock",
          type: "checkbox",
        }
      ]
    : []
)


        ]
    },
    {
        heading: "Material Details",
        name: "materialDtlList",
        colCnt: 4,
        children: [
            {
                name: "assetId",
                label: "Asset ID",
                type: "text",
                span: 2,
                // required: true
            },
             {
                name: "assetCode",
                label: "Asset Code",
                type: "text",
                span: 2,
                // required: true
            },
            {
                name: "assetDesc",
                label: "Asset Description",
                type: "text",
                span: 2,
                // required: true
            },
            {
                name: "materialCode",
                label: "Material Code",
                type: "text",
                span: 2,
                // required: true
            },
            {
                name: "materialDesc",
                label: "Material Description",
                type: "text",
                span: 2,
                // required: true
            },
            {
                name: "uomId",
                label: "UOM",
                type: "text",
                span: 2,
                required: true
            },
            {
                name: "locatorId",
                label: "Locator",
                type: "select",
                options: ldd,
                span: 2,
                required: true
            },
            {
                name: "unitPrice",
                label: "Unit Price",
                type: "text",
                required: true
            },...(formData.isDepreciationDisabled ? [] : [{
                name: "depriciationRate",
                label: "Depreciation Rate",
                type: "text",
                required: true
            }]),
            {
                name: "bookValue",
                label: "Book Value",
                type: "text",
                required: true,
                disabled: true,
            
            },
            
            {
                name: "receivedQuantity",
                label: "Received Quantity",
                type: "text",
                required: true
            },
            {
                name: "acceptedQuantity",
                label: "Accepted Quantity",
                type: "text",
                required: true
            },
        ]
    },
    {
        heading: "Custodian Details",
        fieldList: [
            {
                label: "Custodian Id",
                name: "indentorName",
                disabled: true,
                type: "text"
            }
            ,  {
                label: "Custodian Name",
                name: "custodianName",
                type: "text",
                disabled: true
            }
        ]
    }
];

const igpGrnFields = [
    {
        heading: "Order Details",
        colCnt: 5,
        fieldList: [
            {
                name: "grnType",
                label: "GRN Type",
                type: "select",
                required: true,
                options: [
                    {
                        value: "GI",
                        label: "GI"
                    },
                    {
                        value: "IGP",
                        label: "IGP"
                    }
                ],
            },
            {
                name: "giNo",
                label: "Enter Process No",
                type: "search",
                required: true,
                span: 2
            },
            {
                name: "grnNo",
                label: "GRN No",
                type: "search",
             //   disabled: true,
                span: 2
            },
            {
                name: "grnDate",
                label: "GRN Date",
                type: "date",
                required: true
            },
            // {
            //     name: "installationDate",
            //     label: "Installation Date",
            //     type: "date",
            //     required: true
            // },
            // {
            //     name: "commissioningDate",
            //     label: "Commission Date",
            //     type: "date",
            //     required: true
            // }
        ]
    },
    {
        heading: "Material Details",
        name: "materialDtlList",
        colCnt: 8,
        children: [
            {
                name: "assetId",
                label: "Asset ID",
                type: "text",
                span: 2,
                // required: true
            },
            {
                name: "assetDesc",
                label: "Asset Description",
                type: "text",
                span: 3,
                // required: true
            },
            {
                name: "materialCode",
                label: "Material Code",
                type: "text",
                span: 2,
                // required: true
            },
            {
                name: "materialDesc",
                label: "Material Description",
                type: "text",
                span: 3,
                // required: true
            },
            {
                name: "uomId",
                label: "UOM",
                type: "text",
                span: 1,
                required: true
            },
            {
                name: "locatorId",
                label: "Locator",
                type: "select",
                options: locatorMaster,
                span: 2,
                required: true
            },
            // {
            //     name: "bookValue",
            //     label: "Book Value",
            //     type: "text",
            //     required: true
            // },
            // {
            //     name: "receivedQuantity",
            //     label: "Received Quantity",
            //     type: "text",
            //     required: true
            // },
            {
                name: "acceptedQuantity",
                label: "Quantity",
                type: "text",
                required: true
            },
            // {
            //     name: "depriciationRate",
            //     label: "Depreciation Rate",
            //     type: "text",
            //     required: true
            // }
        ]
    },
    {
        heading: "Custodian Details",
        fieldList: [
            {
                label: "Custodian Id",
                name: "indentorName",
                type: "text",
                disabled: true
            }
        ]
    }
]

const materialInFields = [
    {
        heading: "Order Details",
        colCnt: 5,
        fieldList: [
            {
                name: "grnType",
                label: "GRN Type",
                type: "select",
                required: true,
                options: [
                    {
                        value: "GI",
                        label: "GI"
                    },
                    {
                        value: "IGP",
                        label: "IGP"
                    },
                    {
                        value: "materialIn",
                        label: "Material Inward"
                    }
                ],
            },
            {
                name: "giNo",
                label: "Enter Process No",
                type: "search",
                required: true,
                span: 2
            },
            {
                name: "grnNo",
                label: "GRN No",
                type: "search",
             //   disabled: true,
                span: 2
            },
            {
                name: "grnDate",
                label: "GRN Date",
                type: "date",
                required: true
            },
            // {
            //     name: "installationDate",
            //     label: "Installation Date",
            //     type: "date",
            //     required: true
            // },
            // {
            //     name: "commissioningDate",
            //     label: "Commission Date",
            //     type: "date",
            //     required: true
            // }
        ]
    },
    {
        heading: "Material Details",
        name: "materialDtlList",
        colCnt: 8,
        children: [
            {
                name: "assetId",
                label: "Asset ID",
                type: "text",
                span: 1,
                disabled: true
                // required: true
            },
            {
                name: "materialDesc",
                label: "Asset Description",
                type: "text",
                span: 2,
                disabled: true
                // required: true
            },
            {
                name: "materialCode",
                label: "Material Code",
                type: "text",
                span: 2,
                disabled: true
                // required: true
            },
            {
                name: "materialDesc",
                label: "Material Description",
                type: "text",
                span: 2,
                disabled: true,
                // required: true
            },
            {
                name: "uomId",
                label: "UOM",
                type: "text",
                span: 1,
                required: true
            },
            {
                name: "locatorId",
                label: "Locator",
                type: "select",
                options: ldd,
                span: 2,
                required: true
            },
            
            // {
            //   name: "unitPrice",
            //   label: "Unit Price",
            //   type: "text",
            //   required: true,
            // },
            // {
            //     name: "bookValue",
            //     label: "Book Value",
            //     type: "text",
            //     required: true,
            
            // },
            // {
            //     name: "bookValue",
            //     label: "Book Value",
            //     type: "text",
            //     required: true
            // },
            // {
            //     name: "receivedQuantity",
            //     label: "Received Quantity",
            //     type: "text",
            //     required: true
            // },
            {
                name: "quantity",
                label: "Quantity",
                type: "text",
                required: true
            },
            // {
            //     name: "depriciationRate",
            //     label: "Depreciation Rate",
            //     type: "text",
            //     required: true
            // }
        ]
    },
    {
        heading: "Custodian Details",
        fieldList: [
            {
                label: "Custodian Id",
                name: "indentorName",
                type: "text",
                disabled: true
            },
              {
                label: "Custodian Name",
                name: "custodianName",
                type: "text",
                disabled: true
            }
        ]
    }
]




  return (
    <Card className="a4-container" ref={printRef}>
      <Heading title="Goods Receipt Note" />
      <CustomForm formData={formData} onFinish={onFinish}>
        {formData.grnType === "GI" &&
          renderFormFields(
            grvFields({ ...formData, isDepreciationDisabled }),
            handleChange,
            formData,
            "",
            null,
            setFormData,
            handleSearch
          )}
        {formData.grnType === "IGP" &&
          renderFormFields(
            igpGrnFields,
            handleChange,
            formData,
            "",
            null,
            setFormData,
            handleSearch
          )}

          {formData.grnType === "materialIn" && (
            <>
            {/* <MaterialSearch itemsArray={materialMaster} setFormData={setFormData} /> */}
            {renderFormFields(materialInFields, handleChange, formData, "", null, setFormData, handleSearch)}
            </>
          )}

        {/* {renderFormFields(grvFields, handleChange, formData, "", null, setFormData, handleSearch)} */}
        <ButtonContainer
          onFinish={onFinish}
          formData={formData}
          draftDataName="grnDraft"
          submitBtnLoading={submitBtnLoading}
          submitBtnEnabled
          printBtnEnabled
          draftBtnEnabled
          handlePrint={handlePrint}
        />
      </CustomForm>
      <CustomModal
        isOpen={modalOpen}
        setIsOpen={setModalOpen}
        title="Goods Receipt Note"
        processNo={formData?.grnNo}
      />
    </Card>
  );
};

export default Grn;
