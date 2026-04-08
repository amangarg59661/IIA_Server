import React, { useState, useEffect, useCallback } from 'react';
import {
  Checkbox, InputNumber, Select, Tag, Spin, Radio
} from 'antd';
import { LoadingOutlined, CheckCircleFilled } from '@ant-design/icons';
import axios from 'axios';

const { Option } = Select;

const ConditionBuilder = ({ value, onChange }) => {
  const parseValue = (v) => {
    if (!v) return {};
    try { return typeof v === 'string' ? JSON.parse(v) : v; }
    catch { return {}; }
  };

  const [config, setConfig] = useState(() => parseValue(value));
  const [activeKeys, setActiveKeys] = useState(() => Object.keys(parseValue(value)));

  const [locations, setLocations] = useState([]);
  const [categories, setCategories] = useState([]);
  const [committees, setCommittees] = useState([]);
  const [modesOfProcurement, setModesOfProcurement] = useState([]);
  const [loadingLocations, setLoadingLocations] = useState(false);
  const [loadingRemote, setLoadingRemote] = useState(false);

  const emit = useCallback((nextConfig, nextKeys) => {
    const filtered = {};
    nextKeys.forEach((k) => {
      if (nextConfig[k] !== undefined && nextConfig[k] !== '' && nextConfig[k] !== null) {
        filtered[k] = nextConfig[k];
      }
    });
    if (onChange) {
      onChange(Object.keys(filtered).length ? JSON.stringify(filtered, null, 2) : null);
    }
  }, [onChange]);

  // Fetch locations from API
  useEffect(() => {
    let cancelled = false;
    const fetchLocations = async () => {
      setLoadingLocations(true);
      try {
        const res = await axios.get('/api/master/locations');
        const data = res.data?.responseData || res.data?.data || [];
        if (!cancelled && data.length) {
          setLocations(data.map(l => ({ value: l.locationCode || l, label: l.locationName || l })));
        } else throw new Error('empty');
      } catch {
        if (!cancelled) setLocations([
          { value: 'BANGALORE', label: 'Bangalore' },
          { value: 'NON_BANGALORE', label: 'Non-Bangalore' },
          { value: 'MUMBAI', label: 'Mumbai' },
          { value: 'DELHI', label: 'Delhi' },
          { value: 'CHENNAI', label: 'Chennai' },
        ]);
      } finally {
        if (!cancelled) setLoadingLocations(false);
      }
    };
    fetchLocations();
    return () => { cancelled = true; };
  }, []);

  // Fetch categories, committees, modes of procurement
  useEffect(() => {
    let cancelled = false;
    const fetchOthers = async () => {
      setLoadingRemote(true);
      try {
        try {
          const res = await axios.get('/api/master/material-categories');
          const data = res.data?.responseData || res.data?.data || [];
          if (!cancelled && data.length) setCategories(data.map(c => ({ value: c.categoryCode || c, label: c.categoryName || c })));
        } catch {
          if (!cancelled) setCategories([
            { value: 'COMPUTER', label: 'Computer' },
            { value: 'NON_COMPUTER', label: 'Non-Computer' },
          ]);
        }
        try {
          const res = await axios.get('/api/master/committees');
          const data = res.data?.responseData || res.data?.data || [];
          if (!cancelled && data.length) setCommittees(data.map(c => ({ value: c.committeeCode || c, label: c.committeeName || c })));
        } catch {
          if (!cancelled) setCommittees([
            { value: 'TECHNO_FINANCIAL', label: 'Techno-Financial Committee' },
            { value: 'PURCHASE_COMMITTEE', label: 'Purchase Committee' },
            { value: 'COMPUTER_COMMITTEE', label: 'Computer Committee' },
            { value: 'WORKS_COMMITTEE', label: 'Works Committee' },
          ]);
        }
        try {
          const res = await axios.get('/api/master/modes-of-procurement');
          const data = res.data?.responseData || res.data?.data || [];
          if (!cancelled && data.length) setModesOfProcurement(data.map(m => ({ value: m.code || m, label: m.name || m })));
        } catch {
          if (!cancelled) setModesOfProcurement([
            { value: 'GEM', label: 'GeM (Government e-Marketplace)' },
            { value: 'OPEN_TENDER', label: 'Open Tender' },
            { value: 'LIMITED_TENDER', label: 'Limited Tender' },
            { value: 'SINGLE_TENDER', label: 'Single Tender' },
            { value: 'PROPRIETARY', label: 'Proprietary Purchase' },
            { value: 'RATE_CONTRACT', label: 'Rate Contract' },
            { value: 'DIRECT_PURCHASE', label: 'Direct Purchase' },
            { value: 'EMERGENCY_PURCHASE', label: 'Emergency Purchase' },
          ]);
        }
      } finally {
        if (!cancelled) setLoadingRemote(false);
      }
    };
    fetchOthers();
    return () => { cancelled = true; };
  }, []);

  const updateField = (key, val) => {
    const next = { ...config, [key]: val };
    setConfig(next);
    emit(next, activeKeys);
  };

  const toggleKey = (key, defaultVal) => {
    let nextKeys, nextConfig;
    if (activeKeys.includes(key)) {
      nextKeys = activeKeys.filter(k => k !== key);
      nextConfig = { ...config };
      delete nextConfig[key];
    } else {
      nextKeys = [...activeKeys, key];
      nextConfig = { ...config, [key]: defaultVal };
    }
    setConfig(nextConfig);
    setActiveKeys(nextKeys);
    emit(nextConfig, nextKeys);
  };

  const isActive = (key) => activeKeys.includes(key);

  // ── Accent colors per condition ──────────────────────────────────────────
  const ACCENTS = {
    amount: '#52c41a',
    category: '#1677ff',
    location: '#722ed1',
    project: '#fa8c16',
    mode: '#13c2c2',
    committee: '#eb2f96',
    bidType: '#2f54eb',
    indentCount: '#faad14',
    role: '#f5222d',
    sanctionLimit: '#fa541c',
    department: '#389e0d',
  };

  // ── Style helpers ────────────────────────────────────────────────────────
  const rowStyle = (active, accent) => ({
    border: active ? `1.5px solid ${accent}` : '1.5px solid #f0f0f0',
    borderRadius: 10,
    marginBottom: 10,
    background: '#fff',
    boxShadow: active ? `0 2px 12px ${accent}28` : '0 1px 4px rgba(0,0,0,0.05)',
    transition: 'all 0.22s ease',
    overflow: 'hidden',
  });

  const headerStyle = (active, accent) => ({
    display: 'flex',
    alignItems: 'center',
    gap: 10,
    padding: '13px 16px',
    background: active ? `${accent}0d` : '#fafafa',
    borderBottom: active ? `1px solid ${accent}22` : 'none',
    cursor: 'pointer',
    userSelect: 'none',
  });

  const contentStyle = { padding: '16px 18px', background: '#fff' };

  const inputLabel = {
    fontSize: 11,
    fontWeight: 600,
    color: '#8c8c8c',
    textTransform: 'uppercase',
    letterSpacing: 0.5,
    marginBottom: 5,
    display: 'block',
  };

  // ── Generic row component ────────────────────────────────────────────────
  const ConditionRow = ({ fieldKey, label, sublabel, icon, defaultVal, accent = '#1677ff', children }) => {
    const active = isActive(fieldKey);
    return (
      <div style={rowStyle(active, accent)}>
        <div style={headerStyle(active, accent)} onClick={() => toggleKey(fieldKey, defaultVal)}>
          <Checkbox
            checked={active}
            onChange={() => toggleKey(fieldKey, defaultVal)}
            onClick={e => e.stopPropagation()}
          />
          <span style={{ fontSize: 18, lineHeight: 1, flexShrink: 0 }}>{icon}</span>
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ fontWeight: 600, fontSize: 13, color: active ? accent : '#434343', lineHeight: 1.4 }}>
              {label}
            </div>
            {sublabel && (
              <div style={{ fontSize: 11, color: '#8c8c8c', marginTop: 1 }}>{sublabel}</div>
            )}
          </div>
          {active && <CheckCircleFilled style={{ color: accent, fontSize: 16, flexShrink: 0 }} />}
        </div>
        {active && <div style={contentStyle}>{children}</div>}
      </div>
    );
  };

  // ── Amount: one checkbox → two side-by-side inputs ───────────────────────
  const AmountField = () => {
    const active = isActive('minAmount') || isActive('maxAmount');
    const accent = ACCENTS.amount;

    const handleToggle = () => {
      if (active) {
        const nextKeys = activeKeys.filter(k => k !== 'minAmount' && k !== 'maxAmount');
        const nextConfig = { ...config };
        delete nextConfig.minAmount;
        delete nextConfig.maxAmount;
        setConfig(nextConfig);
        setActiveKeys(nextKeys);
        emit(nextConfig, nextKeys);
      } else {
        const nextKeys = [...activeKeys, 'minAmount', 'maxAmount'];
        const nextConfig = { ...config, minAmount: null, maxAmount: null };
        setConfig(nextConfig);
        setActiveKeys(nextKeys);
        emit(nextConfig, nextKeys);
      }
    };

    return (
      <div style={rowStyle(active, accent)}>
        <div style={headerStyle(active, accent)} onClick={handleToggle}>
          <Checkbox checked={active} onChange={handleToggle} onClick={e => e.stopPropagation()} />
          <span style={{ fontSize: 18, lineHeight: 1, flexShrink: 0 }}>💰</span>
          <div style={{ flex: 1 }}>
            <div style={{ fontWeight: 600, fontSize: 13, color: active ? accent : '#434343', lineHeight: 1.4 }}>
              Amount Range
            </div>
            <div style={{ fontSize: 11, color: '#8c8c8c', marginTop: 1 }}>
              Filter by minimum and / or maximum order value
            </div>
          </div>
          {active && <CheckCircleFilled style={{ color: accent, fontSize: 16, flexShrink: 0 }} />}
        </div>

        {active && (
          <div style={{ ...contentStyle, display: 'flex', gap: 16 }}>
            {/* Min */}
            <div style={{ flex: 1 }}>
              <span style={inputLabel}>Minimum (₹)</span>
              <InputNumber
                style={{ width: '100%' }}
                min={0}
                step={10000}
                formatter={v => v ? `₹ ${v}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',') : ''}
                parser={v => v.replace(/₹\s?|(,*)/g, '')}
                value={config.minAmount}
                onChange={v => updateField('minAmount', v)}
                placeholder="e.g. 50,000"
              />
              <div style={{ fontSize: 11, color: '#bfbfbf', marginTop: 4 }}>Leave blank — no lower limit</div>
            </div>
            {/* Max */}
            <div style={{ flex: 1 }}>
              <span style={inputLabel}>Maximum (₹)</span>
              <InputNumber
                style={{ width: '100%' }}
                min={0}
                step={10000}
                formatter={v => v ? `₹ ${v}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',') : ''}
                parser={v => v.replace(/₹\s?|(,*)/g, '')}
                value={config.maxAmount}
                onChange={v => updateField('maxAmount', v)}
                placeholder="e.g. 5,00,000"
              />
              <div style={{ fontSize: 11, color: '#bfbfbf', marginTop: 4 }}>Leave blank — no upper limit</div>
            </div>
          </div>
        )}
      </div>
    );
  };

  // ── JSON preview ─────────────────────────────────────────────────────────
  const JsonPreview = () => {
    if (!value) return null;
    return (
      <div style={{ marginTop: 16, borderRadius: 8, overflow: 'hidden', border: '1px solid #f0f0f0' }}>
        <div style={{
          background: '#1e1e1e',
          padding: '6px 14px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
        }}>
          <span style={{ fontSize: 11, color: '#858585', fontFamily: 'monospace' }}>condition_config.json</span>
          <Tag color="green" style={{ margin: 0, fontSize: 10 }}>LIVE PREVIEW</Tag>
        </div>
        <pre style={{
          margin: 0,
          padding: '12px 14px',
          background: '#252526',
          fontSize: 12,
          color: '#9cdcfe',
          fontFamily: 'monospace',
          maxHeight: 160,
          overflowY: 'auto',
          lineHeight: 1.6,
        }}>
          {value}
        </pre>
      </div>
    );
  };

  // ── Main render ──────────────────────────────────────────────────────────
  return (
    <div>
      <div style={{
        fontSize: 12,
        color: '#8c8c8c',
        marginBottom: 12,
        padding: '8px 12px',
        background: '#f5f5f5',
        borderRadius: 6,
        borderLeft: '3px solid #d9d9d9',
      }}>
        Check the conditions you want to apply. All active conditions must match (AND logic).
      </div>

      <AmountField />

      <ConditionRow
        fieldKey="materialCategory"
        label="Material Category"
        sublabel="Route by type of material being purchased"
        icon="🖥️"
        defaultVal="COMPUTER"
        accent={ACCENTS.category}
      >
        {loadingRemote ? <Spin indicator={<LoadingOutlined />} /> : (
          <Radio.Group
            value={config.materialCategory}
            onChange={e => updateField('materialCategory', e.target.value)}
            optionType="button"
            buttonStyle="solid"
          >
            {(categories.length ? categories : [
              { value: 'COMPUTER', label: 'Computer' },
              { value: 'NON_COMPUTER', label: 'Non-Computer' },
            ]).map(c => <Radio.Button key={c.value} value={c.value}>{c.label}</Radio.Button>)}
          </Radio.Group>
        )}
      </ConditionRow>

      <ConditionRow
        fieldKey="location"
        label="Location"
        sublabel="Route by consignee / delivery location"
        icon="📍"
        defaultVal=""
        accent={ACCENTS.location}
      >
        {loadingLocations ? (
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, color: '#8c8c8c', fontSize: 13 }}>
            <Spin indicator={<LoadingOutlined style={{ fontSize: 14 }} />} />
            Loading locations from server…
          </div>
        ) : (
          <Select
            style={{ width: '100%' }}
            value={config.location || undefined}
            onChange={v => updateField('location', v)}
            placeholder="Search and select a location"
            showSearch
            optionFilterProp="label"
            allowClear
          >
            {locations.map(l => (
              <Option key={l.value} value={l.value} label={l.label}>{l.label}</Option>
            ))}
          </Select>
        )}
      </ConditionRow>

      <ConditionRow
        fieldKey="projectBased"
        label="Project Based"
        sublabel="Whether the indent is under a sanctioned project"
        icon="📁"
        defaultVal={true}
        accent={ACCENTS.project}
      >
        <Radio.Group
          value={config.projectBased}
          onChange={e => updateField('projectBased', e.target.value)}
          optionType="button"
          buttonStyle="solid"
        >
          <Radio.Button value={true}>Under Project</Radio.Button>
          <Radio.Button value={false}>Not Under Project</Radio.Button>
        </Radio.Group>
      </ConditionRow>

      <ConditionRow
        fieldKey="modeOfProcurement"
        label="Mode of Procurement"
        sublabel="Route by how the procurement is being done"
        icon="🛒"
        defaultVal=""
        accent={ACCENTS.mode}
      >
        {loadingRemote ? <Spin indicator={<LoadingOutlined />} /> : (
          <Select
            style={{ width: '100%' }}
            value={config.modeOfProcurement || undefined}
            onChange={v => updateField('modeOfProcurement', v)}
            placeholder="Select mode of procurement"
            showSearch
            optionFilterProp="label"
            allowClear
          >
            {modesOfProcurement.map(m => (
              <Option key={m.value} value={m.value} label={m.label}>{m.label}</Option>
            ))}
          </Select>
        )}
      </ConditionRow>
      

      <ConditionRow
        fieldKey="department"
        label="Department"
        sublabel="Department responsible for handling the tender"
        icon="🏢"
        defaultVal="PURCHASE"
        accent={ACCENTS.department}
      >
        <Radio.Group
          value={config.department}
          onChange={e => updateField('department', e.target.value)}
          optionType="button"
          buttonStyle="solid"
        >
          <Radio.Button value="PURCHASE">Purchase</Radio.Button>
          <Radio.Button value="STORES">Stores</Radio.Button>
        </Radio.Group>
      </ConditionRow>

      

      

      
    </div>
  );
};

export default ConditionBuilder;
