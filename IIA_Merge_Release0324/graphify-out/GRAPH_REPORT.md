# Graph Report - C:/Users/DELL/Downloads/graphify-tender-tmp  (2026-05-19)

## Corpus Check
- 1 files · ~3,000 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 61 nodes · 117 edges · 8 communities
- Extraction: 97% EXTRACTED · 3% INFERRED · 0% AMBIGUOUS · INFERRED: 3 edges (avg confidence: 0.72)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- [[_COMMUNITY_Community 0 Accept, Acknowledge Clarification, Confirm Evaluation|Community 0: Accept, Acknowledge Clarification, Confirm Evaluation]]
- [[_COMMUNITY_Community 1 Initiate Evaluation, Bid Type Dropdown, Tender Type Dropdown|Community 1: Initiate Evaluation, Bid Type Dropdown, Tender Type Dropdown]]
- [[_COMMUNITY_Community 2 Amount Category Under 10 Lakh, Single Bid, Case 1 LimitedProprietary  Single Bid  Under 10 Lakh  Single Indent|Community 2: Amount Category: Under 10 Lakh, Single Bid, Case 1: Limited/Proprietary | Single Bid | Under 10 Lakh | Single Indent]]
- [[_COMMUNITY_Community 3 Financial Document, Technical Document, Technical Evaluation|Community 3: Financial Document, Technical Document, Technical Evaluation]]
- [[_COMMUNITY_Community 4 Purchase Order Vendor Mapping, Purchase Order Vendor Selection, Financial Evaluation|Community 4: Purchase Order Vendor Mapping, Purchase Order Vendor Selection, Financial Evaluation]]
- [[_COMMUNITY_Community 5 Case 2 GeMOpenGlobal  Single Bid  Under 10 Lakh  Single Indent, Case 8 GeMOpenGlobal  Double Bid  Under 10 Lakh  Multiple Indent, Send Quotation for Evaluation|Community 5: Case 2: GeM/Open/Global | Single Bid | Under 10 Lakh | Single Indent, Case 8: GeM/Open/Global | Double Bid | Under 10 Lakh | Multiple Indent, Send Quotation for Evaluation]]
- [[_COMMUNITY_Community 6 Double Bid  Two Bid, Case 5 LimitedProprietary  Double Bid  Under 10 Lakh  Single Indent, Case 6 GeMOpenGlobal  Double Bid  Under 10 Lakh  Single Indent|Community 6: Double Bid / Two Bid, Case 5: Limited/Proprietary | Double Bid | Under 10 Lakh | Single Indent, Case 6: GeM/Open/Global | Double Bid | Under 10 Lakh | Single Indent]]
- [[_COMMUNITY_Community 7 workflowTransition.Status = Approved, Tender Evaluation Process, Status Pending Initiation|Community 7: workflowTransition.Status = Approved, Tender Evaluation Process, Status: Pending Initiation]]

## God Nodes (most connected - your core abstractions)
1. `Purchase Personnel` - 12 edges
2. `Indent Creator` - 11 edges
3. `Technical Evaluation` - 11 edges
4. `Financial Evaluation` - 10 edges
5. `Case 6: GeM/Open/Global | Double Bid | Under 10 Lakh | Single Indent` - 9 edges
6. `Case 8: GeM/Open/Global | Double Bid | Under 10 Lakh | Multiple Indent` - 9 edges
7. `Tender Evaluation Process Flow - All 8 Cases` - 8 edges
8. `Case 5: Limited/Proprietary | Double Bid | Under 10 Lakh | Single Indent` - 8 edges
9. `Case 7: Limited/Proprietary | Double Bid | Under 10 Lakh | Multiple Indent` - 8 edges
10. `Amount Category: Under 10 Lakh` - 8 edges

## Surprising Connections (you probably didn't know these)
- `Indent Creator` --semantically_similar_to--> `Purchase Personnel`  [INFERRED] [semantically similar]
  Tender_Evaluation_Process_Flow_All_8_Cases_Final.md → Tender_Evaluation_Process_Flow_All_8_Cases_Final.md  _Bridges community 1 → community 0_
- `Tender Evaluation Process Flow - All 8 Cases` --references--> `Case 2: GeM/Open/Global | Single Bid | Under 10 Lakh | Single Indent`  [EXTRACTED]
  Tender_Evaluation_Process_Flow_All_8_Cases_Final.md → Tender_Evaluation_Process_Flow_All_8_Cases_Final.md  _Bridges community 2 → community 5_
- `Tender Evaluation Process Flow - All 8 Cases` --references--> `Case 5: Limited/Proprietary | Double Bid | Under 10 Lakh | Single Indent`  [EXTRACTED]
  Tender_Evaluation_Process_Flow_All_8_Cases_Final.md → Tender_Evaluation_Process_Flow_All_8_Cases_Final.md  _Bridges community 2 → community 6_
- `Case 1: Limited/Proprietary | Single Bid | Under 10 Lakh | Single Indent` --references--> `Indent Creator`  [EXTRACTED]
  Tender_Evaluation_Process_Flow_All_8_Cases_Final.md → Tender_Evaluation_Process_Flow_All_8_Cases_Final.md  _Bridges community 2 → community 0_
- `Case 2: GeM/Open/Global | Single Bid | Under 10 Lakh | Single Indent` --implements--> `Single Indent`  [EXTRACTED]
  Tender_Evaluation_Process_Flow_All_8_Cases_Final.md → Tender_Evaluation_Process_Flow_All_8_Cases_Final.md  _Bridges community 5 → community 6_

## Hyperedges (group relationships)
- **Single Bid Cases (Cases 1-4)** — case_1, case_2, case_3, case_4, bid_type_single [EXTRACTED 1.00]
- **Double Bid Cases (Cases 5-8)** — case_5, case_6, case_7, case_8, bid_type_double [EXTRACTED 1.00]
- **Limited/Proprietary Procurement Cases** — case_1, case_3, case_5, case_7, procurement_mode_limited [EXTRACTED 1.00]
- **GeM/Open/Global Procurement Cases** — case_2, case_4, case_6, case_8, procurement_mode_gem [EXTRACTED 1.00]
- **Single Indent Cases** — case_1, case_2, case_5, case_6, indent_type_single [EXTRACTED 1.00]
- **Multiple Indent Cases** — case_3, case_4, case_7, case_8, indent_type_multiple [EXTRACTED 1.00]
- **Single Indent Evaluation Workflow (Indent Creator → SPO)** — role_indent_creator, role_spo, action_confirm_evaluation, status_pending_spo_approval, status_tender_evaluation_completed [EXTRACTED 1.00]
- **Multiple Indent Evaluation Workflow (Purchase Personnel → SPO)** — role_purchase_personnel, role_spo, action_confirm_evaluation, status_pending_spo_approval, status_tender_evaluation_completed [EXTRACTED 1.00]
- **Double Bid Full Workflow** — process_technical_evaluation, process_financial_evaluation, doc_technical_comparison_sheet, doc_financial_comparison_sheet, role_spo [EXTRACTED 1.00]
- **Clarification Process** — action_seek_clarification, action_acknowledge_clarification, concept_clarification_history, portal_vendor, concept_line_level_seek_clarification, concept_table_level_seek_clarification [EXTRACTED 1.00]
- **Vendor Bid Submission** — role_vendor, portal_vendor, doc_technical_document, doc_financial_document [EXTRACTED 1.00]
- **GeM Pre-Evaluation Bid Upload** — role_purchase_personnel, process_gem_tender_evaluation, doc_technical_document, doc_financial_document, concept_send_quotation_for_evaluation [EXTRACTED 1.00]
- **All 8 Cases Under 10 Lakh** — case_1, case_2, case_3, case_4, case_5, case_6, case_7, case_8, amount_under_10_lakh [EXTRACTED 1.00]

## Communities (8 total, 0 thin omitted)

### Community 0 - "Community 0: Accept, Acknowledge Clarification, Confirm Evaluation"
Cohesion: 0.21
Nodes (14): Accept, Acknowledge Clarification, Confirm Evaluation, Reject, Seek Clarification, Clarification History, Line-level Seek Clarification, Seek Revision / Clarification (SPO Action) (+6 more)

### Community 1 - "Community 1: Initiate Evaluation, Bid Type Dropdown, Tender Type Dropdown"
Cohesion: 0.2
Nodes (11): Initiate Evaluation, Bid Type Dropdown, Tender Type Dropdown, Comparison Sheet, Financial Comparison Sheet, Technical Comparison Sheet, Astro Portal, Purchase Personnel (+3 more)

### Community 2 - "Community 2: Amount Category: Under 10 Lakh, Single Bid, Case 1: Limited/Proprietary | Single Bid | Under 10 Lakh | Single Indent"
Cohesion: 0.47
Nodes (9): Amount Category: Under 10 Lakh, Single Bid, Case 1: Limited/Proprietary | Single Bid | Under 10 Lakh | Single Indent, Case 3: Limited/Proprietary | Single Bid | Under 10 Lakh | Multiple Indent, Case 4: GeM/Open/Global | Single Bid | Under 10 Lakh | Multiple Indent, Case 7: Limited/Proprietary | Double Bid | Under 10 Lakh | Multiple Indent, Tender Evaluation Process Flow - All 8 Cases, Multiple Indent (+1 more)

### Community 3 - "Community 3: Financial Document, Technical Document, Technical Evaluation"
Cohesion: 0.29
Nodes (8): Financial Document, Technical Document, Technical Evaluation, Vendor, Status: Pending Financial Comparison Sheet Upload, Status: Pending SPO Technical Approval, Status: Pending Technical Approval from Indentor, Status: Pending Technical Approval from Purchase Personnel

### Community 4 - "Community 4: Purchase Order Vendor Mapping, Purchase Order Vendor Selection, Financial Evaluation"
Cohesion: 0.29
Nodes (7): Purchase Order Vendor Mapping, Purchase Order Vendor Selection, Financial Evaluation, Status: Pending Financial Approval from Indentor, Status: Pending Financial Approval from Purchase Personnel, Status: Pending SPO Financial Approval, Status: Tender Evaluation Completed

### Community 5 - "Community 5: Case 2: GeM/Open/Global | Single Bid | Under 10 Lakh | Single Indent, Case 8: GeM/Open/Global | Double Bid | Under 10 Lakh | Multiple Indent, Send Quotation for Evaluation"
Cohesion: 0.6
Nodes (5): Case 2: GeM/Open/Global | Single Bid | Under 10 Lakh | Single Indent, Case 8: GeM/Open/Global | Double Bid | Under 10 Lakh | Multiple Indent, Send Quotation for Evaluation, GeM Tender Evaluation (Pre-Evaluation Bid Upload), Procurement Mode: GeM / Open / Global

### Community 6 - "Community 6: Double Bid / Two Bid, Case 5: Limited/Proprietary | Double Bid | Under 10 Lakh | Single Indent, Case 6: GeM/Open/Global | Double Bid | Under 10 Lakh | Single Indent"
Cohesion: 0.67
Nodes (4): Double Bid / Two Bid, Case 5: Limited/Proprietary | Double Bid | Under 10 Lakh | Single Indent, Case 6: GeM/Open/Global | Double Bid | Under 10 Lakh | Single Indent, Single Indent

### Community 7 - "Community 7: workflowTransition.Status = Approved, Tender Evaluation Process, Status: Pending Initiation"
Cohesion: 0.67
Nodes (3): workflowTransition.Status = Approved, Tender Evaluation Process, Status: Pending Initiation

## Knowledge Gaps
- **20 isolated node(s):** `Acknowledge Clarification`, `Status: Pending Initiation`, `Status: Pending Approval from Indentor`, `Status: Pending Approval from Purchase Personnel`, `Status: Pending SPO Approval` (+15 more)
  These have ≤1 connection - possible missing edges or undocumented components.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Purchase Personnel` connect `Community 1: Initiate Evaluation, Bid Type Dropdown, Tender Type Dropdown` to `Community 0: Accept, Acknowledge Clarification, Confirm Evaluation`, `Community 2: Amount Category: Under 10 Lakh, Single Bid, Case 1: Limited/Proprietary | Single Bid | Under 10 Lakh | Single Indent`, `Community 4: Purchase Order Vendor Mapping, Purchase Order Vendor Selection, Financial Evaluation`, `Community 5: Case 2: GeM/Open/Global | Single Bid | Under 10 Lakh | Single Indent, Case 8: GeM/Open/Global | Double Bid | Under 10 Lakh | Multiple Indent, Send Quotation for Evaluation`?**
  _High betweenness centrality (0.345) - this node is a cross-community bridge._
- **Why does `Indent Creator` connect `Community 0: Accept, Acknowledge Clarification, Confirm Evaluation` to `Community 1: Initiate Evaluation, Bid Type Dropdown, Tender Type Dropdown`, `Community 2: Amount Category: Under 10 Lakh, Single Bid, Case 1: Limited/Proprietary | Single Bid | Under 10 Lakh | Single Indent`, `Community 5: Case 2: GeM/Open/Global | Single Bid | Under 10 Lakh | Single Indent, Case 8: GeM/Open/Global | Double Bid | Under 10 Lakh | Multiple Indent, Send Quotation for Evaluation`, `Community 6: Double Bid / Two Bid, Case 5: Limited/Proprietary | Double Bid | Under 10 Lakh | Single Indent, Case 6: GeM/Open/Global | Double Bid | Under 10 Lakh | Single Indent`?**
  _High betweenness centrality (0.329) - this node is a cross-community bridge._
- **Why does `Technical Evaluation` connect `Community 3: Financial Document, Technical Document, Technical Evaluation` to `Community 1: Initiate Evaluation, Bid Type Dropdown, Tender Type Dropdown`, `Community 2: Amount Category: Under 10 Lakh, Single Bid, Case 1: Limited/Proprietary | Single Bid | Under 10 Lakh | Single Indent`, `Community 5: Case 2: GeM/Open/Global | Single Bid | Under 10 Lakh | Single Indent, Case 8: GeM/Open/Global | Double Bid | Under 10 Lakh | Multiple Indent, Send Quotation for Evaluation`, `Community 6: Double Bid / Two Bid, Case 5: Limited/Proprietary | Double Bid | Under 10 Lakh | Single Indent, Case 6: GeM/Open/Global | Double Bid | Under 10 Lakh | Single Indent`?**
  _High betweenness centrality (0.190) - this node is a cross-community bridge._
- **What connects `Acknowledge Clarification`, `Status: Pending Initiation`, `Status: Pending Approval from Indentor` to the rest of the system?**
  _20 weakly-connected nodes found - possible documentation gaps or missing edges._