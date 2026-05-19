import React, { useState } from 'react';
import { Button, Upload, Space, Modal, message } from 'antd';
import { UploadOutlined, DeleteOutlined, EyeOutlined } from '@ant-design/icons';

const FileUpload = ({ documentName, fileType, onChange, value, fileName }) => {
  const [previewVisible, setPreviewVisible] = useState(false);
  const [previewTitle, setPreviewTitle] = useState('');
  const [previewContent, setPreviewContent] = useState('');

  // Function to get base64 representation of file for preview
  const getBase64 = (file) => {
    return new Promise((resolve, reject) => {
      // Make sure file is a valid File or Blob object
      if (!file || !(file instanceof File || file instanceof Blob)) {
        console.log("Invalid file object:", file);
        reject(new Error('Not a valid file'));
        return;
      }
      
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result);
      reader.onerror = (error) => reject(error);
    });
  };
/*
  // Handle file preview
  const handlePreview = async () => {
    console.log("handlePreview called",value)
    if (!value?.file) return;
    
    try {
      let preview;
      // Use existing preview if available
      if (value.file.preview) {
        preview = value.file.preview;
      } 
      // Use URL if available
      else if (value.file.url) {
        preview = value.file.url;
      } 
      // Generate preview from file object
      else if (value.file.originFileObj) {
        preview = await getBase64(value.file.originFileObj);
      }
      
      if (!preview) {
        message.error('Unable to generate preview');
        return;
      }
      
      setPreviewContent(preview);
      setPreviewTitle(value.file.name || 'Document Preview');
      setPreviewVisible(true);
    } catch (error) {
      console.error('Preview error:', error);
      message.error('Failed to generate preview');
    }
  };
*/
const handlePreview = async () => {
  console.log("handlePreview called", value);
  
  // Exit early if no file data exists
  //if (!value?.file || !value.file.file) return;
  if (!value?.file || !value.file.file) {
    message.error('No file selected for preview');
    return;
  }

  const file = value.file.file;  // Adjusting based on the provided structure

  try {
    let preview;

    // Check if the file already has a preview
    if (file.preview) {
      preview = file.preview;
    } 
    // Check if the file URL exists (in case of uploaded files from cloud storage)
    else if (file.url) {
      preview = file.url;
    } 
    // Otherwise, generate preview for file (base64)
    else if (file.originFileObj) {
      preview = await getBase64(file.originFileObj);  // Using your getBase64 function
    }

    // If no preview could be generated, show an error
    if (!preview) {
      message.error('Unable to generate preview');
      return;
    }

    // Set the preview content and title
    setPreviewContent(preview);
    setPreviewTitle(file.name || 'Document Preview');
    
    // Display the preview modal
    setPreviewVisible(true);

  } catch (error) {
    console.error('Preview error:', error);
    message.error('Failed to generate preview');
  }
};


  // Handle file upload
  const handleUpload = async (info) => {
    try {
      const file = info.file;
      if (!file) return;
      
      // Generate preview at upload time
      let preview;
      try {
        preview = await getBase64(file);
      } catch (error) {
        console.error('Preview generation error:', error);
      }
      
      const fileData = {
        fileType: file.type,
        file: {
          uid: file.uid || Date.now().toString(),
         // name: fileName || file.name,
          name: file.name,
          status: 'done',
          type: file.type,
          originFileObj: file,
          preview: preview,
        }
      };
      
      onChange(fileData);
    } catch (error) {
      console.error('Upload error:', error);
      message.error('Failed to upload file');
    }
  };

  // Handle file deletion
  const handleDelete = () => {
    onChange(null);
    setPreviewContent('');
    setPreviewVisible(false);
  };

  const isUploaded = !!value?.file;

  return (
    <div className="mb-4">
      <div className="font-semibold mb-2">{documentName}</div>
      <Space>
        {!isUploaded ? (
          <Upload 
            showUploadList={false}
            customRequest={({ file, onSuccess }) => {
              setTimeout(() => {
                onSuccess("ok");
              }, 0);
            }}
            onChange={(info) => {
              // Make sure we're getting the file with originFileObj
              if (info.file.status === 'done') {
                handleUpload({ file: info.file.originFileObj || info.file });
              }
            }}
            accept={fileType === 'pdf' ? '.pdf' : fileType === 'image' ? 'image/*' : undefined}
          >
            <Button icon={<UploadOutlined />}>Upload</Button>
          </Upload>
        ) : (
          <>
            {/*<Button icon={<EyeOutlined />} onClick={handlePreview}>Preview</Button>*/}
            <Button danger onClick={handleDelete}>Delete</Button>
            <span className="ml-2">
              {value?.file?.name || fileName} {/* Display fileName if available */}
            </span>
          </>
        )}
      </Space>

      <Modal
        open={previewVisible}
        title={previewTitle}
        footer={null}
        onCancel={() => setPreviewVisible(false)}
        width={800}
      >
        {/*previewContent && (
          value?.file?.type?.includes('pdf') || fileType === 'pdf' ? 
            <iframe src={previewContent} width="100%" height="500px" title={previewTitle} /> :
            <img alt={previewTitle} style={{ width: '100%', maxHeight: '70vh', objectFit: 'contain' }} src={previewContent} />
        )*/}
         {previewContent && (
        fileType === 'pdf' || value?.file?.type?.includes('pdf') ? (
        <iframe src={previewContent} width="100%" height="500px" title={previewTitle} />
        ) : (
        <img alt={previewTitle} style={{ width: '100%', maxHeight: '70vh', objectFit: 'contain' }} src={previewContent} />
        )
        )}
    

      </Modal>
    </div>
  );
};

export default FileUpload;