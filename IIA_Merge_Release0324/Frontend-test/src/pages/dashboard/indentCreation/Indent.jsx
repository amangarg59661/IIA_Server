import React, { useRef, useState, useEffect } from "react";
import { Card, Form, message } from "antd";
import { useReactToPrint } from "react-to-print";
import axios from "axios";
import { useSelector } from "react-redux";
import Heading from "../../../components/DKG_Heading";
import CustomForm from "../../../components/DKG_CustomForm";
import { renderFormFields } from "../../../utils/CommonFunctions";
import ButtonContainer from "../../../components/ButtonContainer";
import CustomModal from "../../../components/CustomModal";
import { IndentDetails } from "./InputFields";

const materialDtlObj = {
  materialCode: "",
        materialDescription: "",
        materialCategory: "",
        materialSubCategory: "",
        uom: "",
        unitPrice: "",
        currency: "",
        totalPrice: ""
}

const Indent = () => {
  const [form] =Form.useForm();
  const printRef = useRef();
  const [modalOpen, setModalOpen] = useState(false);
  const [submitBtnLoading, setSubmitBtnLoading] = useState(false);
  const [materialDescriptionMap, setMaterialDescriptionMap] = useState({});

  // Redux selectors for user and location details
  const { userName, email, mobileNumber, token } = useSelector(
    (state) => state.auth
  );
  const auth = useSelector((state) => state.auth);
  const actionPerformer = auth.userId;

  // Data states for fetched data
  const [locations, setLocations] = useState([]);
  const [projects, setProjects] = useState([]);
  const [materialDetailsMap, setMaterialDetailsMap] = useState({});

  // Main form data state
  const [formData, setFormData] = useState({ materialDetails: [{}], consignesLocation: "Bangalore" });
  const [materialOptions, setMaterialOptions] = useState([]);
  const [materialDescriptionOptions, setMaterialDescriptionOptions] = useState([]);
  const [materials, setMaterials] = useState([]);
  const [vendors, setVendors] = useState([]);

  // --- Dynamic Field Population ---
  const populateDropdowns = async () => {
    try {
      const [locationResponse, projectResponse, materialResponse, vendorResponse] =
        await Promise.all([
          axios.get("/api/location-master"),
          axios.get("/api/project-master"),
          axios.get("/api/material-master"),
          axios.get("/api/vendor-master")
        ]);

      // Format options for dropdowns
      const formattedLocations = (
        locationResponse.data?.responseData || []
      ).map((location) => ({
        label: location.locationName,
        value: location.locationName,
      }));

      const formattedVendors = (vendorResponse.data?.responseData || []).map(vendor => ({
        label: vendor.vendorName,
        value: vendor.vendorName,
      }));
      setVendors(formattedVendors);

      const formattedProjects = (projectResponse.data?.responseData || []).map(
        (project) => ({
          label: project.projectNameDescription,
          value: project.projectNameDescription,
        })
      );

      const materials = materialResponse.data?.responseData || [];
      setMaterials(materials);
      const formattedMaterials = materials.map((material) => ({
        label: material.materialName,
        value: material.materialCode,
      }));
      const formattedMaterialDescriptions = materials.map((material) => ({
        label: material.description,
        value: material.description,
      }));
      
      const materialMap = {};
      materials.forEach((material) => {
        materialMap[material.materialCode] = {
          materialDescription: material.description,
          uom: material.uom,
          unitPrice: material.unitPrice,
          materialCategory: material.category,
          materialSubCategory: material.subCategory,
          currency: material.currency,
        };
      });
      const materialDescriptionMap = {};
      materials.forEach((material) => {
        materialDescriptionMap[material.description] = {
          materialCode: material.materialCode,
          uom: material.uom,
          unitPrice: material.unitPrice,
          materialCategory: material.category,
          materialSubCategory: material.subCategory,
          currency: material.currency,
        };
      });
      setMaterialDetailsMap(materialMap); // Already done
      setMaterialDescriptionMap(materialDescriptionMap); // NEW STATE
      setMaterialOptions(formattedMaterials);
      setMaterialDescriptionOptions(formattedMaterialDescriptions);

      // Set options in state
      setLocations(formattedLocations);
      setProjects(formattedProjects);
      //   setMaterialOptions(formattedMaterials);
    } catch (error) {
      console.error("Error fetching dropdown data:", error);
      message.error(
        error?.response?.data?.responseStatus?.message ||
          "Failed to fetch dropdown data."
      );
    }
  };

  

  // --- handleChange Function ---
  const handleChange = async (name, value) => {
    if (Array.isArray(name)) {
      const [section, index, field] = name;

      if (section === "materialDetails") {
        const updatedMaterials = [...formData.materialDetails]

        // Check for materialCategory and modeOfProcurement consistency
        if (field === "materialCategory" || field === "modeOfProcurement") {
          // Check if there are other materials with different values
          const hasInconsistentValues = updatedMaterials.some((material, i) => {
            return i !== index && material[field] && material[field] !== value;
          });

          if (hasInconsistentValues) {
            updatedMaterials[index] = materialDtlObj;
            message.error(`All materials must have the same ${field === 'materialCategory' ? 'Material Category' : 'Mode of Procurement'}`);
            setFormData((prev) => ({
             ...prev,
              materialDetails: updatedMaterials,
            }));
            return;
          }

          // If passed validation, update all materials with the same value
          updatedMaterials.forEach(material => {
            material[field] = value;
          });
        } else {
          // Handle other field changes as before
          if (field === "materialCode") {
            const materialData = materialDetailsMap[value] || {};
            const quantity = updatedMaterials[index].quantity || 0;

            updatedMaterials[index] = {
              ...updatedMaterials[index],
              materialCode: value,
              materialDescription: materialData.materialDescription || "",
              materialCategory: materialData.materialCategory || "",
              materialSubCategory: materialData.materialSubCategory || "",
              uom: materialData.uom || "",
              unitPrice: materialData.unitPrice || 0,
              currency: materialData.currency || "",
              totalPrice: (materialData.unitPrice || 0) * quantity,
            };

            // Remove selected material from options
            const updatedMaterialOptions = materialOptions.filter(option => 
              !formData.materialDetails.some(material => 
                material.materialCode === option.value && material.materialCode !== updatedMaterials[index].materialCode
              )
            );
            setMaterialOptions(updatedMaterialOptions);

            // Ensure materialCategory consistency after auto-fill
            const newCategory = materialData.materialCategory;
            if (newCategory) {
              const hasInconsistentCategory = updatedMaterials.some((material, i) => {
                return i !== index && material.materialCategory && material.materialCategory !== newCategory;
              });

              if (hasInconsistentCategory) {
                message.error('All materials must have the same Material Category');
                return;
              }

              // Update all materials with the same category
              updatedMaterials.forEach(material => {
                material.materialCategory = newCategory;
              });
            }
          }
        }

        setFormData((prev) => ({
          ...prev,
          materialDetails: updatedMaterials,
        }));
      }
    } else {
      // Top-level fields
      setFormData((prev) => ({
        ...prev,
        [name]: value,
      }));
    }
  };

  // --- onFinish Function ---
  const onFinish = async () => {
    if(formData.isPreBidMeetingRequired && !formData.tentativeMeetingDate){
      message.error("Please enter the tentative meeting date");
      return;
    }
    const payload = { ...formData, createdBy: actionPerformer , projectName: formData.projectName, fileType: "Indent" };

    try {
      setSubmitBtnLoading(true);
      const { data } = await axios.post("/api/indents", payload);

      setFormData({
        ...formData,
        indentId: data?.responseData?.indentId,
      });

      localStorage.removeItem("indentDraft");
      setModalOpen(true);
    } catch (error) {
      message.error(
        error?.response?.data?.responseStatus?.message ||
          "Failed to save Indent."
      );
      ;
    } finally {
      setSubmitBtnLoading(false);
    }
  };

  // --- handleSearch Function ---
  const handleSearch = async (value) => {
    try {
      const { data } = await axios.get(
        `/api/indents/gbyId`, {params : {value ? value : formData.indentId}}
      );

      setFormData({
        ...data?.responseData,
      });
    } catch (error) {
      ;
      message.error(
        error?.response?.data?.responseStatus?.message || "Error fetching data."
      );
    }
  };

  // --- Draft Saving and Loading ---
  useEffect(() => {
    const indentDraft = localStorage.getItem("indentDraft");
    if (indentDraft) {
      setFormData(JSON.parse(indentDraft));
      message.success("Form loaded from draft.");
    }
  }, []);

  useEffect(() => {
    localStorage.setItem("indentDraft", JSON.stringify(formData));
  }, [formData]);

  // --- Printing Function ---
  const handlePrint = useReactToPrint({
    content: () => printRef.current,
  });

  // --- Populate Dropdowns on Mount ---
  useEffect(() => {
    populateDropdowns();
  }, []);

  // --- Prepare Hydrated Indent Details ---
  const hydratedIndentDetails = IndentDetails.map((section) => {
    if (section.fieldList) {
      return {
        ...section,
        fieldList: section.fieldList.map((field) => {
          if (field.name === "consignesLocation")
            return { ...field, options: locations };
          if (field.name === "projectName")
            return { ...field, options: projects };
          if (field.name === "preBidMeetingVenue")
            return {...field, options: locations };
          return field;
        }),
      };
    }
    if (section.children) {
      return {
        ...section,
        children: section.children.map((child) => {
            if (child.name === "vendorNames") {
                return {
                  ...child,
                  options: vendors,  // Remove this line
                  props: { vendors }
                };
              }
          if (child.name === "materialCode")
            return { ...child, options: materialOptions };
          else if (child.name === "materialDescription")
            return {...child, options: materialDescriptionOptions };
          return child;
        }),
      };
    }
    return section;
  });

  const addMaterialRow = () => {

    const materialDetails = formData.materialDetails;
    const lastMaterial = materialDetails[materialDetails.length - 1];
    if(!lastMaterial.materialCode && !lastMaterial.materialCategory && !lastMaterial.materialSubCategory && !lastMaterial.uom && !lastMaterial.unitPrice && !lastMaterial.currency && !lastMaterial.totalPrice){
      message.error("Please fill all the fields of the last row before adding a new row");
      return;
    }
    setFormData((prev) => ({
     ...prev,
      materialDetails: [...prev.materialDetails, {
        materialCode: "",
        materialDescription: "",
        materialCategory: "",
        materialSubCategory: "",
        uom: "",
        unitPrice: "",
        currency: "",
        totalPrice: ""
      }],
    }));
  };

  ;

  // --- Auto Populate Indentor Information Based on Login Info--
  useEffect(() => {
    setFormData({
      ...formData,
      indentorEmailAddress: email,
      indentorMobileNo: mobileNumber,
      indentorName: userName,
    });
  }, []);

  useEffect(() => {
    
    form.setFieldsValue(formData);
  }, [form, formData])

  return (
    <Card className="a4-container" ref={printRef}>
      <Heading title="Indent Form" />
      <CustomForm formData={formData} onFinish={onFinish} customForm={form}>
        {renderFormFields(
          hydratedIndentDetails,
          handleChange,
          formData,
          "",
          null,
          setFormData,
          handleSearch,
          addMaterialRow
        )}
        <ButtonContainer
          onFinish={onFinish}
          formData={formData}
          draftDataName="indentDraft"
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
        title="Indent"
        processNo={formData?.indentId}
      />
    </Card>
  );
};

export default Indent;