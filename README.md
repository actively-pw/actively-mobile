## About
Actively is a exercise tracking app that uses GPS to record route, location and speed of the user. Basic statistics like: speed, avg speed and time are calculated in real time. Route is also displayed on map in real time while recording.

## Screens
#### Recording screen
User can select from multiple disciplines. There are also intuitive animated recording controls. Recording data is persisted at all times. Recording is also launched in foreground service, so the recording is running no matter what the user is doing on the phone. 

![Recording screen](https://github.com/actively-pw/actively-mobile/assets/59282537/9e1f398f-9a19-497a-aa15-90656fc91d80)

<img src="https://github.com/actively-pw/actively-mobile/assets/59282537/c5f39387-73e3-42dd-9be2-1faa28f9b40b" alt="drawing" width="200" />

After activity was recorded then it has to be named and saved. Recorded activity can be discarded.

<img src="https://github.com/actively-pw/actively-mobile/assets/59282537/5d16060a-d04f-4ace-9969-f73e7ce23143" alt="drawing" width="200"/>


#### Home screen
List of recorded activites with basic stats, timestamp and discipline type. The map is static map image generated with `Mapbox Static Image API`

<img src="https://github.com/actively-pw/actively-mobile/assets/59282537/2f11e970-949d-49fd-bfc1-46ccd37880d1" alt="drawing" width="200"/>

#### Statistics screen
Summary statistics: weekly avg, year-to-date and all-time for each discipline.

<img src="https://github.com/actively-pw/actively-mobile/assets/59282537/0032ebaf-74b5-401e-a934-d18bd7cf8ffb" alt="drawing" width="200"/>


## Technologies
- Kotlin
- Jetpack Compose
- Koin
- Paging3
- Mapbox SDK
- Mapbox Static Images API
- SQLDelight
- Ktor
- Coil
- JUnit
- Kotest
- Mockk
