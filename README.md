# financial-data-toolkit
Toolkit which helps me maintain my personal finance (mainly in google spreadsheets). Main features:
* gathering data from various financial APIs
* updating google spreadsheets with financial data

### Configuration

All config files should be placed under `/config` directory.

Currently, the following config files are supported:
* dataimport.yaml - to import data from google spreadsheet
* dataexport.yaml - to export stocks/currencies quotation data to google spreadsheets

### Usage

`./gradlew installDist` rebuilds local library