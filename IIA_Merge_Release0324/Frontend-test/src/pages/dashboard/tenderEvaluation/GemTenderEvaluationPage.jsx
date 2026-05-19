import React, { useCallback, useEffect, useState } from "react";
import {
  Button, Form, Input, message, Modal, Select, Space,
  Spin, Table, Tag, Typography, Upload,
} from "antd";
import { PlusOutlined, SendOutlined, UploadOutlined } from "@ant-design/icons";
import axios from "axios";
import { useSelector } from "react-redux";

const { Title } = Typography;
const { Option } = Select;

/**
 * GeM / Open / Global Tender Evaluation.
 * Purchase Personnel adds vendors and documents manually before sending
 * the entries into the main Tender Evaluation flow.
 *
 * Route: /procurement/gem-tender-evaluation
 */
const GemTenderEvaluationPage = () => {
  const auth = useSelector((s) => s.auth);

  const [tenders,       setTenders]       = useState([]);
  const [selectedTender, setSelectedTender] = useState(null);
  const [entries,       setEntries]       = useState([]);
  const [loading,       setLoading]       = useState(false);
  const [addDlgOpen,    setAddDlgOpen]    = useState(false);
  const [addForm]                          = Form.useForm();
  const [sendLoading,   setSendLoading]   = useState(false);

  // ── Load approved tenders ─────────────────────────────────────────────────
  const loadTenders = useCallback(async () => {
    try {
      const res = await axios.get("/getApprovedTender");
      const all = res.data?.responseData || [];
      // GeM/Open/Global only
      const gem = all.filter((t) => {
        const mode = (t.modeOfProcurement || "").toLowerCase();
        return mode.includes("gem") || mode.includes("open") || mode.includes("global");
      });
      setTenders(gem);
    } catch {
      message.error("Failed to load tenders.");
    }
  }, []);

  useEffect(() => { loadTenders(); }, [loadTenders]);

  // ── Load entries for selected tender ─────────────────────────────────────
  const loadEntries = useCallback(async (tenderId) => {
    if (!tenderId) return;
    setLoading(true);
    try {
      const res = await axios.get(`/api/gem-tender-evaluation/${tenderId}`);
      setEntries(res.data?.responseData || []);
    } catch {
      message.error("Failed to load vendor entries.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { loadEntries(selectedTender); }, [selectedTender, loadEntries]);

  // ── File to base64 ────────────────────────────────────────────────────────
  const fileToBase64 = (file) =>
    new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload  = () => resolve(reader.result);
      reader.onerror = reject;
      reader.readAsDataURL(file);
    });

  // ── Add vendor ────────────────────────────────────────────────────────────
  const handleAddVendor = async (values) => {
    const techDoc = values.technicalDoc?.file?.originFileObj;
    const finDoc  = values.financialDoc?.file?.originFileObj;
    const techB64 = techDoc ? await fileToBase64(techDoc) : null;
    const finB64  = finDoc  ? await fileToBase64(finDoc)  : null;

    try {
      await axios.post(`/api/gem-tender-evaluation/${selectedTender}/add-vendor`, {
        vendorName:           values.vendorName,
        technicalDocFileName: techB64,
        financialDocFileName: finB64,
        addedByUserId:        auth.userId,
      });
      message.success("Vendor added.");
      setAddDlgOpen(false);
      addForm.resetFields();
      loadEntries(selectedTender);
    } catch (e) {
      message.error(e?.response?.data?.message || "Failed to add vendor.");
    }
  };

  // ── Upload tech/fin doc for existing entry ────────────────────────────────
  const handleUpload = async (id, file, isFinancial) => {
    const base64 = await fileToBase64(file);
    const endpoint = isFinancial
      ? `/api/gem-tender-evaluation/upload-financial/${id}`
      : `/api/gem-tender-evaluation/upload-technical/${id}`;
    try {
      await axios.put(endpoint, {
        [isFinancial ? "financialDocFileName" : "technicalDocFileName"]: base64,
        userId: auth.userId,
      });
      message.success("Document uploaded.");
      loadEntries(selectedTender);
    } catch {
      message.error("Upload failed.");
    }
    return false;
  };

  // ── Send for evaluation ───────────────────────────────────────────────────
  const handleSendForEvaluation = async () => {
    if (!selectedTender) return;
    setSendLoading(true);
    try {
      const res = await axios.post(
        `/api/gem-tender-evaluation/${selectedTender}/send-for-evaluation`,
        { actionByUserId: auth.userId }
      );
      message.success(res.data?.responseData?.message || "Sent for evaluation.");
      loadEntries(selectedTender);
    } catch (e) {
      message.error(e?.response?.data?.message || "Send failed.");
    } finally {
      setSendLoading(false);
    }
  };

  // ── Table columns ──────────────────────────────────────────────────────────
  const columns = [
    { title: "Vendor Name", dataIndex: "vendorName", key: "vendorName" },
    { title: "Vendor ID",   dataIndex: "vendorId",   key: "vendorId",
      render: (v) => v || "--" },
    {
      title: "Technical Document",
      key: "techDoc",
      render: (_, r) => r.technicalDocFileName
        ? <Button size="small" type="link"
            href={`/file/view/Tender/${r.technicalDocFileName}`} target="_blank">View</Button>
        : <Upload beforeUpload={(f) => handleUpload(r.id, f, false)} showUploadList={false}>
            <Button size="small" icon={<UploadOutlined />}>
              Upload Technical Document
            </Button>
          </Upload>,
    },
    {
      title: "Financial Document",
      key: "finDoc",
      render: (_, r) => r.financialDocFileName
        ? <Button size="small" type="link"
            href={`/file/view/Tender/${r.financialDocFileName}`} target="_blank">View</Button>
        : <Upload beforeUpload={(f) => handleUpload(r.id, f, true)} showUploadList={false}>
            <Button size="small" icon={<UploadOutlined />}>
              Upload Financial Document
            </Button>
          </Upload>,
    },
    {
      title: "Current Status",
      dataIndex: "status",
      key: "status",
      render: (s) => (
        <Tag color={s === "SENT_FOR_EVALUATION" ? "green" : "orange"}>
          {s === "SENT_FOR_EVALUATION" ? "Sent for Evaluation" : "Pending"}
        </Tag>
      ),
    },
  ];

  return (
    <div className="p-4">
      <Title level={4}>GeM / Open / Global Tender Evaluation</Title>

      <Space wrap className="mb-4">
        <Select
          showSearch
          allowClear
          style={{ width: 380 }}
          placeholder="Select Tender ID"
          value={selectedTender}
          onChange={setSelectedTender}
          optionFilterProp="label"
          options={tenders.map((t) => ({
            label: `${t.tenderId} - ${t.titleOfTender || t.tenderId}`,
            value: t.tenderId,
          }))}
        />

        {selectedTender && (
          <>
            <Button
              icon={<PlusOutlined />}
              onClick={() => setAddDlgOpen(true)}
            >
              Add Vendor
            </Button>
            <Button
              type="primary"
              icon={<SendOutlined />}
              loading={sendLoading}
              onClick={handleSendForEvaluation}
              disabled={entries.every((e) => e.sentForEvaluation)}
            >
              Send Quotation for Evaluation
            </Button>
          </>
        )}
      </Space>

      <Spin spinning={loading}>
        <Table
          dataSource={entries}
          columns={columns}
          rowKey="id"
          size="small"
          bordered
          pagination={false}
          locale={{ emptyText: "No vendors added yet. Add vendors and upload documents." }}
        />
      </Spin>

      {/* ── Add Vendor Dialog ── */}
      <Modal
        title="Add Vendor"
        open={addDlgOpen}
        onCancel={() => { setAddDlgOpen(false); addForm.resetFields(); }}
        onOk={() => addForm.submit()}
        okText="Add"
      >
        <Form form={addForm} layout="vertical" onFinish={handleAddVendor}>
          <Form.Item
            name="vendorName"
            label="Vendor Name"
            rules={[{ required: true, message: "Vendor name is required" }]}
          >
            <Input placeholder="Enter vendor name" />
          </Form.Item>
          <Form.Item name="technicalDoc" label="Upload Technical Document">
            <Upload beforeUpload={() => false} maxCount={1}>
              <Button icon={<UploadOutlined />}>Choose Technical Document</Button>
            </Upload>
          </Form.Item>
          <Form.Item name="financialDoc" label="Upload Financial Document">
            <Upload beforeUpload={() => false} maxCount={1}>
              <Button icon={<UploadOutlined />}>Choose Financial Document</Button>
            </Upload>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default GemTenderEvaluationPage;
