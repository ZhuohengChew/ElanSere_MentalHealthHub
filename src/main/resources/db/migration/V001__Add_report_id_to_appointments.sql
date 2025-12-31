-- Add report_id column to appointments table
ALTER TABLE appointments ADD COLUMN report_id BIGINT NULL;

-- Add foreign key constraint to link appointments to reports (concerns table)
ALTER TABLE appointments 
ADD CONSTRAINT fk_appointments_report_id 
FOREIGN KEY (report_id) REFERENCES concerns(id) ON DELETE SET NULL;

-- Create index on report_id for faster queries
CREATE INDEX idx_appointments_report_id ON appointments(report_id);
