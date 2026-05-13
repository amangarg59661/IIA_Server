import { Checkbox, Form, message, Select, Radio } from "antd";
import { DeleteOutlined,DownloadOutlined } from '@ant-design/icons';
import FormItemInput from "antd/es/form/FormItemInput";
import axios from "axios";
import CustomDatePicker from "../components/DKG_CustomDatePicker";
import CustomInput from "../components/CustomInput";
import CustomSearch from "../components/CustomSearch";
import ImageUploadBase64 from "../components/ImageUploadBas64";
import InputDatePicker from "../components/DatePicker";
import UploadFile from "../components/UploadFile";
import Btn from "../components/DKG_Btn";
import DownloadFile from "../components/DowloadFile";
import CustomIndentSearch from "../components/CustomIndentSearch";
import CustomGprnSearch from "../components/CoustomGprnSearch";


export const apiCall = async (method, url, token, payload = null) => {

  const header = {
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
  };

  try {
    let response;

    if (method === "GET") {
      response = await axios.get(url);
    } else if (method === "POST") {
      response = await axios.post(url, payload);
    }

    // // Check response status code
    // if (response.data.responseStatus.statusCode === 1) {
    //   return response; // Return the data on success
    // } else {
    //   // Throw an error if the status code indicates failure
    //   throw new Error(response.data.responseStatus.message || "Request failed.");
    // }

    return response
  } catch (error) {
    // Display error alert
    message.error(error?.response?.data?.responseStatus?.message || "Some error occurred.");
    // Rethrow the error for the calling function to handle
    
    throw error;
  }
};


  export const handleChange = (fieldName, value, setFormData) => {
    setFormData(prev => {
      return {
        ...prev,
        [fieldName]: value
      }
    })
  }

  export const checkAndConvertToFLoat = (value) => {
    if (value === null || value.trim() === "" || !/^-?\d+(\.\d+)?$/.test(value)) {
      message.error("Invalid number.");
      return{number: null, isFloat: false};
    }

    return {number: parseFloat(value), isFloat: true}
  }

  const sanitizeText = (text) => {
    // return text
    return text.toString().toLowerCase().replace(/\s+/g, '');
  };

  const recursiveSearch = (object, searchText) => {
    for (let key in object) {
      const value = object[key];
      if (typeof value === "object") {
        if (Array.isArray(value)) {
          for (let item of value) {
            if (recursiveSearch(item, searchText)) {
              return true;
            }
          }
        } else {
          if (recursiveSearch(value, searchText)) {
            return true;
          }
        }
      } else if (
        value &&
        sanitizeText(value).includes(searchText)
      ) {
        return true;
      }
    }
    return false;
  };
  
  export const handleSearch = (searchText, itemData, setHook, setSearch=null) => {
    if(searchText !== null){
        const sanitizedText = sanitizeText(searchText);
        if(setSearch)
          setSearch(searchText)
        const filtered = itemData?.filter((parentObject) =>
          recursiveSearch(parentObject, sanitizedText)
      );
      setHook([...filtered]);
    }
    else{
      setHook([...itemData])
    }
  };

  export const convertToCurrency = (amount) => {
    const formattedAmount = amount?.toLocaleString('en-IN', {
      style: 'currency',
      currency: 'INR'
    });
    return formattedAmount
  }

  export const updateFormData = (newItem, setFormData) => {
    console.log("Called UPDATE");
    setFormData((prevValues) => {
      const updatedItems = [
        ...(prevValues.materialDtlList || []),
        {
          ...newItem,
          // noOfDays: prevValues.processType === "NIRP" ? "0" : (newItem.noOfDays ? newItem.noOfDays : "1"),
          // srNo: prevValues.items?.length ? prevValues.items.length + 1 : 1,
        },
      ];
      return { ...prevValues, materialDtlList: updatedItems };
    });
    console.log("DONE UPDATE");
  };
  export const updateFormDataWithSerial = (record, serialNumber, setFormData) => {
  console.log(" updateFormDataWithSerial called for:", serialNumber);

  console.log("record", record);
  const newItem = {
    ...record,
    assetId: record.assetId,
    assetCode: record.assetCode,
    senderLocatorId: record.locatorId,
    serialNo: serialNumber, 
    quantity: 1,
    uniqueKey: `${record.assetCode}_${serialNumber}`,
  };

  setFormData(prev => {
    const existing = prev.materialDtlList || [];

    // remove same asset+serial if exists to prevent duplicate
    const filtered = existing.filter(it => it.uniqueKey !== newItem.uniqueKey);

    return {
      ...prev,
      materialDtlList: [...filtered, newItem],
    };
  });

  console.log("DONE updateFormDataWithSerial", serialNumber);
};



  
  export const itemHandleChange = (fieldName, value, index, setFormData) => {
    setFormData((prevValues) => {
      const updatedItems = [...(prevValues.items || [])];
      
      if (fieldName === "unitPrice" && /^\d*\.?\d*$/.test(value)) {
        updatedItems[index] = {
          ...updatedItems[index],
          [fieldName]: value === "" ? 0 : value,
        };
      } else {
        updatedItems[index] = {
          ...updatedItems[index],
          [fieldName]: value,
        };
      }
  
      return {
        ...prevValues,
        items: updatedItems,
      };
    });
  };
  
  export const removeItem = (index, setFormData) => {
    setFormData((prevValues) => {
      const updatedItems = prevValues.items;
      updatedItems.splice(index, 1);
  
      const updatedItems1 = updatedItems.map((item, key) => {
        return { ...item, srNo: key + 1 };
      });
  
      return { ...prevValues, items: updatedItems1 };
    });
  };  

  const conditonalRender = (field, handleChange, formData, handleSearch) => {
    if (!field || !field.type) {
      throw new Error("Provided field type is missing.");
    }
  
    const { type } = field;
  
    switch (type) {
      case "text":
        return (
          <CustomInput
            label={field?.label}
            name={field?.name}
            required={field?.required}
            disabled={field?.disabled}
            onChange={handleChange}
            className="w-full"
          />
        );
  
        case "date":
            return (
              <InputDatePicker
              required={field?.required}
                label={field?.label}
                name={field?.name}
                disabled={field?.disabled}
                onChange={handleChange}
                defaultValue={formData[field.name]}
                rules={field?.required? [{ required: true, message: `${field?.label} is required` }] : []}
              />
            );

      case "multiImage":
        return (
          <ImageUploadBase64
            label={field?.label}
            name={field?.name}
            required={field?.required}
            disabled={field?.disabled}
            onChange={handleChange}
            value={formData[field.name]}
            multiple={true}
            // accept="image/*"
            accept={field.accept || "image/*"} 
          />
        );
      case "files":
  return (
    <ImageUploadBase64
      label={field?.label}
      name={field?.name}
      required={field?.required}
      disabled={field?.disabled}
      onChange={handleChange}
      value={formData[field.name]}
      // accept="image/*"
      accept={field.accept || "image/*"} 
    />
  ); 
  case "customDropdown":
      if (!field.component) {
        throw new Error("customDropdown type requires a 'component' property.");
      }
      const Component = field.component;
      return (
        <Form.Item key={field.name} label={field.label} required={field.required}>
          <Component
            value={formData[field.name]}
            onChange={(val) => handleChange(field.name, val)}
            onSelect={(item) => handleChange("selectedProcessObj", item)} 
          
          />
        </Form.Item>
      );



      case "image":
        return (
          <ImageUploadBase64
            label={field?.label}
            name={field?.name}
            required={field?.required}
            disabled={field?.disabled}
            onChange={handleChange}
            value={formData[field.name]}
          />
        );

        case "search":
        return (
          <CustomSearch
            label={field?.label}
            name={field?.name}
            required={field?.required}
            disabled={field?.disabled}
            onChange={handleChange}
            onSearch={field?.onSearch ? field.onSearch : handleSearch}
            className="w-full"
          />
        );

        case "select":
          return (
            <Form.Item 
              name={field?.name}
              label={field?.label}
              rules={field?.required ? [{ required: true, message: `${field?.label} is required` }] : []}
            >
              <Select showSearch options={field?.options} disabled={field?.disabled} onChange={(val) => handleChange(field?.name, val)} {...field.props}   filterOption={(input, option) =>
    option?.label?.toLowerCase().includes(input.toLowerCase())
  } />
            </Form.Item>
        );
        case "custom":

  if (typeof field.render === "function") {
    // Extract index from field name if it's an array field like "materialDetails[0].purchaseHistoryButton"
    const fieldNameStr = typeof field.name === 'string' ? field.name : '';
    const match = fieldNameStr.match(/\[(\d+)\]/);
    const index = match ? parseInt(match[1], 10) : null;
    return field.render(index);
  }
  throw new Error("Custom type requires a render function.");

  case "pvselect":
  return (
    <Form.Item
      name={field?.name}
      label={field?.label}
      rules={field?.required ? [{ required: true, message: `${field?.label} is required` }] : []}
    >
      <Select
        showSearch
        options={field?.options}
        filterOption={(input, option) =>
          option.searchText
            ? option.searchText.includes(input.toLowerCase())
            : option.label.toLowerCase().includes(input.toLowerCase())
        }
        disabled={field?.disabled}
        onChange={(value) => handleChange(field?.name, value)}
      />
    </Form.Item>
  );


        case "multiselect":
          return (
            <Form.Item 
              name={field?.name}
              label={field?.label}
              rules={field?.required ? [{ required: true, message: `${field?.label} is required` }] : []}
            >
              <Select mode="multiple" showSearch options={field?.options} disabled={field?.disabled} onChange={(val) => handleChange(field?.name, val)} {...field.props} />
            </Form.Item>
        );
        case "multiIndentselect":
  return (
    <Form.Item 
      name={field?.name}
      label={field?.label}
      rules={field?.required ? [{ required: true, message: `${field?.label} is required` }] : []}
    >
      <Select
        mode="multiple"
        showSearch
        options={field?.options}
        disabled={field?.disabled}
        onChange={(val) => handleChange(field?.name, val)}
        filterOption={(input, option) => {
          const searchTerm = input.toLowerCase();
          return (
            option.value.toLowerCase().includes(searchTerm) ||
            option.projectName?.toLowerCase().includes(searchTerm) ||
            option.indentorName?.toLowerCase().includes(searchTerm) ||
            option.createdDate?.toLowerCase().includes(searchTerm) ||
            (Array.isArray(option.materialDes) &&
              option.materialDes.some((m) =>
                String(m).toLowerCase().includes(searchTerm)
              ))
          );
        }}
        {...field.props}
      />
    </Form.Item>
  );


        case "checkbox":
        return (
        <Form.Item
            name={field?.name}
            label={field?.label}
            valuePropName="checked"
            rules={[{ required: field?.required, message: `${field?.label} is required` }]}
            // rules={[
            //   {
            //     validator: (_, value) => {
            //       if (field?.required && !value) {
            //         alert(`${field?.label} is required`);
            //         return Promise.reject(new Error(`${field?.label} must be checked`));
            //       }
            //       return Promise.resolve();
            //     },
            //   },
            // ]}
        >
            <Checkbox
            disabled={field?.disabled}
            onChange={(e) => handleChange(field?.name, e.target.checked)}
            checked={formData[field.name]}
            />
        </Form.Item>
        );

      case "radio":
        return (
          <Form.Item
            name={field?.name}
            label={field?.label}
            rules={field?.required ? [{ required: true, message: `${field?.label} is required` }] : []}
          >
            <Radio.Group
              disabled={field?.disabled}
              onChange={(e) => handleChange(field?.name, e.target.value)}
              value={formData[field.name]}
              optionType={field?.buttonStyle ? "button" : "default"}
              buttonStyle={field?.buttonStyle || "outline"}
            >
              {field?.options?.map((option, idx) => (
                <Radio key={idx} value={option.value}>
                  {option.label}
                </Radio>
              ))}
            </Radio.Group>
          </Form.Item>
        );

      case "uploadFiles":
        return (
            <Form.Item
              name={field?.name}
              label={field?.label}
              rules={field?.required ? [{ required: true, message: `${field?.label} is required` }] : []}
            >
                <UploadFile
                fileType={field?.fileType}
                onUploadSuccess={(fileName) => handleChange(field?.name, fileName)}
                />
            </Form.Item>
        );
        case "downloadFile":
          return (
          <Form.Item
          label={field?.label}
          name={field?.name}
          key={field?.name}
          rules={field?.required ? [{ required: true, message: `${field?.label} is required` }] : []}
          >
          <DownloadFile
          fileName={field?.fileName}
          fileLabel={field?.downloadText || "Download File"}
          />
          </Form.Item>
        );
        case "indentSearch":
  return (
    <CustomIndentSearch
      label={field?.label}
      name={field?.name}
      searchType={formData.searchType}
      setSearchType={(val) => handleChange("searchType", val)}
      searchValue={formData.searchValue}
      setSearchValue={(val) => handleChange("searchValue", val)}
      onSearch={field?.onSearch}
    />
  );
   case "gprnSearch":
  return (
    <CustomGprnSearch
      label={field?.label}
      name={field?.name}
      searchType={formData.searchType}
      setSearchType={(val) => handleChange("searchType", val)}
      searchValue={formData.searchValue}
      setSearchValue={(val) => handleChange("searchValue", val)}
      onSearch={field?.onSearch}
    />
  );
  case "selectTenderId":
  return (
    <Form.Item
      name={field?.name}
      label={field?.label}
      rules={field?.required ? [{ required: true, message: `${field?.label} is required` }] : []}
    >
      <Select
        showSearch
        options={field?.options}
        disabled={field?.disabled}
        onChange={(val) => {
          handleChange(field?.name, val); // update form
          if (typeof field?.onChange === "function") {
            field.onChange(val); // always pass the latest val
          }
        }}
        filterOption={(input, option) =>
          option?.label?.toLowerCase().includes(input.toLowerCase())
        }
      />
    </Form.Item>
  );





        case "checkboxWithLabelText":
        return (
          <Form.Item
            key={field?.name}
            name={field?.name}
            valuePropName="checked"
            rules={[
        {
          required: field?.required,
        //  message: "Please accept this declaration to proceed.",
        },
      ]}
          >
          <Checkbox
            disabled={field?.disabled}
            onChange={(e) => handleChange(field?.name, e.target.checked)}
            checked={formData[field.name]}
          >
          <span style={{ textAlign: "justify", display: "inline-block" }}>
            {field?.label}
          </span>
          </Checkbox>
         </Form.Item>
        );





        // case "multiselect":
        // return (
        //       <Form.Item
        //         name={field?.name}
        //         label={field?.label}
        //         rules={field?.required ? [{ required: true, message: `${field?.label} is required` }] : []}
        //       >
        //       <Select
        //       mode="multiple"
        //       showSearch
        //       options={field?.options}
        //       disabled={field?.disabled}
        //       onChange={(val) => handleChange(field?.name, val)}  // Ensure 'val' is an array
        //       value={formData[field?.name] || []} // Ensure default value is an empty array
        //       {...field.props}
        //       />
        //     </Form.Item>
        //      );
      default:
        throw new Error("Provided field type doesn't exist.");
    }
  };

  const colClasses = {
    2: "md:grid-cols-2",
    3: "md:grid-cols-3",
    4: "md:grid-cols-4",
    5: "md:grid-cols-5",
    6: "md:grid-cols-6",
    7: "md:grid-cols-7",
    8: "md:grid-cols-8",
    9: "md:grid-cols-9",
    10: "md:grid-cols-10",
  };
 
export const renderFormFields = (detail, handleChange, formData, parentName = "", index = null, setFormData, handleSearch = null, additionalFunc) => {
  const handleDeleteChild = (sectionName, childIndex) => {
    if (!setFormData) {
      console.error('setFormData is required for deletion');
      return;
    }
    
    setFormData(prev => {
      const updatedSection = [...prev[sectionName]];
      updatedSection.splice(childIndex, 1);
      
      return {
        ...prev,
        [sectionName]: updatedSection
      };
    });

    if (additionalFunc && typeof additionalFunc["materialDeselect"] === "function") {
  additionalFunc["materialDeselect"](childIndex);
}
  };

  return (
    <>
      {detail.map((section, sectionIndex) => (
        <div key={sectionIndex} className="mb-4">
          <h1 className="font-semibold">{section?.heading}</h1>
          {section?.fieldList ? (
            <div className={`grid md:gap-x-4 md:gap-y-2 ${colClasses[section.colCnt] || "md:grid-cols-3"}`}>
              {section.fieldList.map((field, fieldIndex) => {
                if (field.shouldShow && !field.shouldShow(formData)) return null;
                return(
                    <div key={fieldIndex} className={`col-span-${field?.span || 1}`}>
                    {conditonalRender(
                        {
                        ...field,
                        name: parentName && index !== null 
                            ? `${parentName}[${index}].${field.name}` 
                            : field.name,
                        }, 
                        handleChange, 
                        formData,
                        handleSearch
                    )}
                    </div>
                );
              })}
            </div>
          ) : section?.children ? (
            // Recursively render children if present
            <div className="border-gray-200 my-2">
              {Array.isArray(formData[section.name]) ? 
                formData[section.name].map((childData, childIndex) => (
                  <div key={childIndex} className="mb-4 p-3 border border-black rounded relative">
                    <DeleteOutlined 
                      onClick={() => handleDeleteChild(section.name, childIndex)}
                      className="absolute top-0 right-0 text-red-500 hover:text-red-700 cursor-pointer text-lg bg-gray-100 p-2"
                    />
                    <div className={`grid md:gap-x-4 md:gap-y-2 ${section.colCnt ? colClasses[section.colCnt] : "md:grid-cols-3"}`}>
                      {section.children.map((child, subIndex) => {
                        // Support shouldShow for child fields (e.g. conversionRate shown only for non-INR)
                        if (child.shouldShow && !child.shouldShow(formData, childIndex)) return null;
                        return (
                        <div key={subIndex} className={`col-span-${child?.span || 1}`}>
                          {conditonalRender(
                            {
                              ...child,
                              name: [section.name, childIndex, child.name]
                            },
                            handleChange,
                            formData
                          )}
                        </div>
                        );
                      })}
                    </div>
                  </div>
                )) 
                : 
                <div className="text-gray-500">No items added yet</div>
              }
            </div>
          ) : null}

{
            section?.addButton && (
              <Btn onClick={additionalFunc["addMaterialSection"]} className="border-darkBlue hover:bg-darkBlueHover text-darkBlue hover:text-darkBlueHover">ADD MORE</Btn>
            )
          }
        </div>
      ))}
    </>
  );
};


                //   (
                //   <div key={childIndex} className="mb-4 p-3 border border-gray-200 rounded">
                //     <h3 className="text-sm font-medium mb-2">Item {childIndex + 1}</h3>
                //     {renderFormFields(
                //       [{ ...section, fieldList: section.children }],
                //       handleChange,
                //       childData,
                //       `${parentName ? `${parentName}[${index}]` : section.name}`,
                //       childIndex
                //     )}
                //   </div>
                // ))
