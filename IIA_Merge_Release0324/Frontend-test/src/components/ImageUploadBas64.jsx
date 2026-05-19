import React, { useEffect, useState } from 'react';
import { Upload, Button, Form } from 'antd';
import { UploadOutlined, DeleteOutlined } from '@ant-design/icons';

const ImageUploadBase64 = ({ value, onChange, required, label, name, multiple = false, accept="image/*,.pdf" }) => {
  const [previewData, setPreviewData] = useState(multiple ? (value || []) : (value || ''));
  const [fileNames, setFileNames] = useState(multiple ? [] : '');

  const handleFileChange = (info) => {
    if (info.file) {
      const file = info.file.originFileObj;
      const reader = new FileReader();

      reader.onloadend = () => {
        const base64String = reader.result;
        if (multiple) {
          const newPreviewData = [...previewData, base64String];
          const newFileNames = [...fileNames, file.name];
          setPreviewData(newPreviewData);
          setFileNames(newFileNames);
          onChange(name, newPreviewData);
        } else {
          setPreviewData(base64String);
          setFileNames(file.name);
          onChange(name, base64String);
        }
      };

      reader.readAsDataURL(file);
    }
  };

  function isPdfBase64(base64) {
    if (!base64) return false;

    try {
      const base64Data = base64.split(',')[1];
      const decoded = atob(base64Data.slice(0, 20)); // decode first ~20 chars
      return decoded.startsWith('%PDF');
    } catch (err) {
      return false;
    }
  }

  const handleDelete = (index) => {
    if (multiple) {
      const newPreviewData = previewData.filter((_, i) => i !== index);
      const newFileNames = fileNames.filter((_, i) => i !== index);
      setPreviewData(newPreviewData);
      setFileNames(newFileNames);
      onChange(name, newPreviewData);
    } else {
      setPreviewData('');
      setFileNames('');
      onChange(name, '');
    }
  };

  useEffect(() => {
    if (value) {
      if (multiple) {
        setPreviewData(value || []);
        setFileNames((value || []).map((_, i) => `File ${i + 1}`));
      } else {
        setPreviewData(value || '');
        setFileNames(value ? 'Uploaded File' : '');
      }
    }
  }, [value, multiple]);

  return (
    <Form.Item
      label={label}
      name={name}
      rules={[{ required: required, message: `${label} is required` }]}
      className="mb-0"
    >
      <div className="flex flex-col gap-2">
        <Upload
          
          multiple={multiple}
          showUploadList={false}
          beforeUpload={(file) => {
            handleFileChange({ file: { originFileObj: file } });
            return false;
          }}
        >
          <Button icon={<UploadOutlined />}>
            {multiple ? 'Add Files' : (previewData ? 'Change File' : 'Upload File')}
          </Button>
        </Upload>

      
        {/*<div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4 max-w-full">*/}
        <div className="flex flex-wrap gap-4 mt-2">
          {multiple ? (
            previewData.map((preview, index) => (
          <div
            key={index}
            className="relative w-[150px] h-[150px] flex-shrink-0 border border-gray-300 rounded overflow-hidden"
          >
            {(fileNames[index]?.toLowerCase().endsWith('.pdf') || isPdfBase64(preview)) ? (
            <iframe
              src={`data:application/pdf;${preview.split(";")[1]}`}
              title={`PDF Preview ${index + 1}`}
              className="w-full h-full"
            />
          ) : (
          <img
            src={preview}
            alt={`Preview ${index + 1}`}
            className="w-full h-full object-contain"
          />
          )}
          <Button
            type="primary"
            danger
            icon={<DeleteOutlined />}
            size="small"
            onClick={() => handleDelete(index)}
            className="absolute top-2 right-2"
          />
        {fileNames[index] && (
          <div className="text-sm text-gray-600 mt-1 truncate">
            {fileNames[index]}
          </div>
        )}
      </div>
    ))
  ) : (
    previewData && (
      <div className="relative w-[150px] h-[150px] flex-shrink-0 border border-gray-300 rounded overflow-hidden">
        {(fileNames?.toLowerCase().endsWith('.pdf') || isPdfBase64(previewData)) ? (
          <iframe
            src={`data:application/pdf;${previewData.split(";")[1]}`}
            title="PDF Preview"
            className="w-full h-full"
          />
        ) : (
          <img
            src={previewData}
            alt="Preview"
            className="w-full h-full object-contain"
          />
        )}
        <Button
          type="primary"
          danger
          icon={<DeleteOutlined />}
          size="small"
          onClick={() => handleDelete()}
          className="absolute top-2 right-2"
        />
        {fileNames && (
          <div className="text-sm text-gray-600 mt-1 truncate">
            {fileNames}
          </div>
        )}
      </div>
    )
  )}
</div>
</div>
    </Form.Item>
  );
};



export default ImageUploadBase64;
