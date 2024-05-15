# financial-data-toolkit
Toolkit which helps me maintain my personal finance (mainly in google spreadsheets). Main features:
* gathering data from various financial APIs
* parsing transaction data from bank statements
* updating google spreadsheets with financial data

### Configuration

`application.properties` in main directory to store Google API credentials:
* `google.clientId`
* `google.clientSecret`

All other config files should be placed under `/config` directory.

Currently, the following config files are supported:
* dataimport.yaml - to import data from google spreadsheet
* dataexport.yaml - to export stocks/currencies quotation data to google spreadsheets
* dictionaries.yaml - to map transaction data to defined list of names of shops 