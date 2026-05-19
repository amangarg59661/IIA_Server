import { Button, message, Tooltip, Input, Popover } from "antd";

import React, { useState } from "react";
import {
  UndoOutlined,
  SaveOutlined,
  CloudDownloadOutlined,
  PrinterOutlined,
  CloseOutlined,
} from "@ant-design/icons";
import { useLocation, useNavigate } from "react-router-dom";

const ButtonContainer = ({
  submitBtnLoading,
  submitBtnEnabled,
  onFinish,
  printBtnEnabled,
  handlePrint,
  draftDataName,        // kept for reset (localStorage clear) only
  draftBtnEnabled,
  draftBtnLoading,      // <-- NEW: loading state from parent
  onDraft,              // <-- NEW: API-based draft save handler from parent
  formData,
  disabled,
  showCancel,
  onCancel,
  cancelButtonText = "Cancel",
}) => {
  const [cancelRemarks, setCancelRemarks] = useState("");
  const navigate = useNavigate();
  const location = useLocation();

  // Reset clears localStorage (draft key) and reloads the page.
  // This is intentional — the draft itself lives on the server now,
  // but the local draft key (if any leftover) is cleared here too.
  const handleReset = () => {
    if (draftDataName) {
      localStorage.removeItem(draftDataName);
    }
    navigate(location.pathname, {
      state: { data: null, itemList: null },
    });
    window.location.reload();
    message.success("The form has been reset, and any saved drafts have been cleared.");
  };

  // Save draft — calls the API via the onDraft prop (no localStorage).
  // Skips all form validations intentionally.
  const handleDraftClick = () => {
    if (!onDraft) {
      message.warning("Draft saving is not configured.");
      return;
    }
    onDraft();
  };

  const handleCancelSubmit = () => {
    if (!cancelRemarks.trim()) {
      message.warning("Please enter remarks to cancel the indent.");
      return;
    }
    onCancel(cancelRemarks);
    setCancelRemarks("");
  };

  return (
    <div className="grid md:grid-cols-4 gap-2">
      <Tooltip title="Clear form">
        <Button
          danger
          icon={<UndoOutlined />}
          onClick={handleReset}
        >
          Reset
        </Button>
      </Tooltip>

      <Tooltip
        title={
          submitBtnEnabled
            ? "Submit form"
            : "Press reset button to enable submit."
        }
      >
        <Button
          htmlType="submit"
          type="primary"
          style={{ backgroundColor: "#4CAF50" }}
          icon={<SaveOutlined />}
          disabled={disabled ? true : submitBtnEnabled ? false : true}
          loading={submitBtnLoading}
        >
          Submit
        </Button>
      </Tooltip>

      {/*
        Draft button intentionally does NOT use htmlType="submit".
        This means Ant Design Form validation is NOT triggered —
        the user can save an incomplete form as a draft at any time.
        The backend generates an indent ID even for drafts.
      */}
      <Tooltip title="Save the form as a draft (no validation required).">
        <Button
          onClick={handleDraftClick}
          type="warning"
          className="border-yellow-300"
          icon={<CloudDownloadOutlined />}
          disabled={disabled ? true : draftBtnEnabled ? false : true}
          loading={!!draftBtnLoading}
        >
          Save draft
        </Button>
      </Tooltip>

      <Tooltip
        title={
          printBtnEnabled ? "Print form" : "Submit the form to enable print."
        }
      >
        <Button
          onClick={handlePrint}
          icon={<PrinterOutlined />}
          disabled={printBtnEnabled ? false : true}
          className="border-blue-300"
        >
          Print
        </Button>
      </Tooltip>

      {showCancel && onCancel && (
        <Popover
          content={
            <div style={{ padding: 12 }}>
              <Input.TextArea
                placeholder="Enter remarks for cancellation"
                rows={3}
                value={cancelRemarks}
                onChange={(e) => setCancelRemarks(e.target.value)}
              />
              <Button
                type="primary"
                onClick={handleCancelSubmit}
                style={{ marginTop: 8 }}
              >
                Submit
              </Button>
            </div>
          }
          title={cancelButtonText}
          trigger="click"
        >
          <Button danger type="default" icon={<CloseOutlined />}>
            {cancelButtonText}
          </Button>
        </Popover>
      )}
    </div>
  );
};

export default ButtonContainer;
