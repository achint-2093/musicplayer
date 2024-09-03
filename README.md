# Music Player

This music player app uses modern Android technologies, including MVVM architecture, Room, Media3, and Hilt. The app allows users to fetch songs from their device, create playlists, play songs, add songs to playlists, and play music by albums and artists.

## Features

1. Uses Media3 for playing music.
2. Implements dependency injection with Hilt.
3. Utilizes Room to create playlists and save songs, avoiding the need to refetch from the device.
4. Employs single activity architecture and MVVM.
5. Allows users to add songs to playlists and play songs by playlist.
6. Navigation using the Navigation Component.
7. Includes sections for albums and artists.


## Screenshot

|                                                                                                                         |                                                                                                               |                                                                                                                |
|-------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------|
| ![songs](https://github.com/user-attachments/assets/39db2ffe-0762-4c3c-92cb-e758d64d8550)                               | ![playlist](https://github.com/user-attachments/assets/a70c5505-fd0d-4355-8023-85fdd8450f5f)                  | ![albums](https://github.com/user-attachments/assets/96ff542f-06f5-4c55-b880-0a8ab15b5849)                     |
| ![artist](https://github.com/user-attachments/assets/7ccf5d9c-36e0-4ab1-b4bc-de7a5831fc7e)                              | ![playlist songs](https://github.com/user-attachments/assets/7b5ad772-4326-4caa-88d5-3ef9ff50b18f)            | ![player](https://github.com/user-attachments/assets/f8584d51-c487-4118-b5b4-5c1cb91e1a70)                     |



## Package Structure

* [`data`](app/src/main/java/com/techuntried/musicplayer/data): Contains Room database, models and repositories.
* [`di`](app/src/main/java/com/techuntried/musicplayer/di): Hilt modules.
* [`ui`](app/src/main/java/com/techuntried/musicplayer/ui): UI layer of the app.
* [`utils`](app/src/main/java/com/techuntried/musicplayer/utils): Utility classes used across the app.


## Build With

[Kotlin](https://kotlinlang.org/):
As the programming language.

[Hilt](https://developer.android.com/training/dependency-injection/hilt-android) :
For injecting dependencies.

[Media3](https://developer.android.com/media/media3) :
For Media Player.

[Room](https://developer.android.com/jetpack/androidx/releases/room) :
To store Songs and Playlists.

[Preferences DataStore](https://developer.android.com/topic/libraries/architecture/datastore) :
To store mini playback details.


## Installation

Simple clone this app and open in Android Studio.

## Project Status

The following features are yet to be implemented:

1. Song and playlist options.
2. Player shuffle and repeat.
3. Marking songs as favorites.
4. App icon.
