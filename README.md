# Screeni 
## Project Creation & Architecture Pattern
We shall follow a Modular Mechanism to implement the flow of the App. This makes the App easier to scale


## Modules
| Module Name              | Description                                                                                                     |
| ------------------------ | --------------------------------------------------------------------------------------------------------------- |
| app                      | This is the main of the Application, this run and keeps activities intact                                       |
| channelcatch             | This is the module responsible for recording screen, internal audio, mic audio based on the Current API Version |
| plinth                   | Base Components Supplier for the whole app                                                                      |
| bucker (not implemented) | A Logger that logs all crashes, debugs, analytics, etc                                                          |
| stockpile                | Repo for all the UI templates                                                                                   |



----------
## Architecture

Minimal/Straightforward architecture as this is supposed to be a demo app

