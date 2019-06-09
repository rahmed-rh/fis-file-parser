--
-- Copyright (C) 2016 Red Hat, Inc.
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

-- PLEASE NOTE THESE ARE OPTIONAL, TO POPULATE DATABASE FOR THIS EXAMPLE, IN YOUR EXAMPLE YOU NEED TO 
-- REMOVE THESE FILES.

DROP TABLE IF EXISTS CAMEL_MESSAGEPROCESSED;
DROP TABLE IF EXISTS BERBER_ZAAK_GEGEVENS;

CREATE TABLE CAMEL_MESSAGEPROCESSED ( 
  processorName VARCHAR(255), 
  messageId VARCHAR(100), 
  createdAt TIMESTAMP
);

CREATE TABLE IF NOT EXISTS BERBER_ZAAK_GEGEVENS(
  ID VARCHAR(36) PRIMARY KEY,
  ZAAKIDENTIFICATIE TEXT NOT NULL,
  BETROKKENE_BARNUMMER TEXT NOT NULL,
  BETROKKENE_NAAM TEXT NOT NULL,
  REGIO VARCHAR(36) NOT NULL
);