-- V14: Rename storage_providers to storage_connections per PO Requirement 8
-- Clarifies that the table represents event storage configuration connections, not provider implementations.
-- See: project-index/06_Database_Design.md — storage_connections entity

ALTER TABLE storage_providers RENAME TO storage_connections;

-- Rename primary key constraint and index
ALTER TABLE storage_connections RENAME CONSTRAINT pk_storage_providers TO pk_storage_connections;
ALTER INDEX idx_storage_event_id RENAME TO idx_storage_conn_event_id;
