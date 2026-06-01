// IconButton.jsx
import React from 'react';
import { Button, Tooltip } from 'antd';
import PropTypes from 'prop-types';

const IconBtn = ({ icon: Icon, tooltipTitle, onClick, className, text, ...props }) => {
  return (
    <Tooltip title={tooltipTitle}>
      <Button
        onClick={onClick}
        className={`p-2 rounded-sm border-none shadow-lg ${className}`}
        {...props}
      >
        <Icon className="text-lg" />
        {text}
      </Button>
    </Tooltip>
  );
};

IconBtn.propTypes = {
  icon: PropTypes.elementType.isRequired,
  tooltipTitle: PropTypes.string,
  onClick: PropTypes.func,
  className: PropTypes.string,
};

export default IconBtn;
