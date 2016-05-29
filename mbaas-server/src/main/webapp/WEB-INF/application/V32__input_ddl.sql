# CREATE TABLE input (
#
#   input_id               VARCHAR(100) NOT NULL,
#   page_id                VARCHAR(100) NOT NULL,
#   label                  VARCHAR(100) NOT NULL,
#   place_holder           VARCHAR(255) NOT NULL,
#   nale                   VARCHAR(100) NOT NULL,
#   type                   VARCHAR(100) NOT NULL,
#   validator_script       VARCHAR(50)  NOT NULL,
#   choice_renderer_script VARCHAR(50)  NOT NULL,
#   choice_provider_script VARCHAR(50)  NOT NULL,
#   button_clict_script    VARCHAR(50)  NOT NULL,
#   eav_value              TIME,
#
#   INDEX (place_holder),
#   INDEX (label),
#   INDEX (nale),
#   INDEX (type),
#   INDEX (validator),
#   INDEX (eav_value),
#   UNIQUE (place_holder, nale, type),
#   INDEX (page_id),
#   PRIMARY KEY (input_id)
# );