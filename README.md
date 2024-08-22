
# GAlbum Application

Welcome to the GAlbum Application! This Kotlin Android app allows you to list and view albums stored on your Android device by utilizing content providers. Below is an overview of how the application is structured and how to get started.

## Features

- **Album Listing:** Displays a list of albums stored on the device.
- **Dynamic UI:** Supports both list and grid views.
- **Permissions Handling:** Manages runtime permissions for accessing device storage.
- **Dependency Injection:** Uses Dagger Hilt for dependency management.

## Getting Started

### Prerequisites

- Android Studio
- Android SDK
- Basic knowledge of Kotlin and Android development
- Basic knowledge of content providers, lifecycle, coroutines and flows

### Setup

1. **Clone the Repository**

   ```bash
   git clone https://github.com/waleedkalyar/GAlbum.git
   ```

2. **Open in Android Studio**

   Import the project into Android Studio and let it sync the Gradle files.

3. **Setup Permissions**

   Ensure that the following permissions are added to your `AndroidManifest.xml`:

   ```xml
   <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
   <uses-permission android:name="android.permission.READ_MEDIA_VISUAL_USER_SELECTED" />
   <uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />
   <uses-permission
        android:name="android.permission.READ_EXTERNAL_STORAGE"
        android:maxSdkVersion="32" />
   ```

   Runtime permissions are also handled using extension functions. Permissions must be granted to make app working properly. without permissions app is blank.

4. **Dependency Injection**

   The project uses Dagger Hilt for dependency injection.

### Application Architecture

1. **Gallery Data Source**

    - **Class:** `GalleryDataSource`
    - **Functions:**
        - `findImagesByAlbumId()`
        - `findVideosByAlbumId()`
        - `findAllImageVideoAlbumsFlow()`
        - `findImageVideosByBucketIdFlow(bucketId: String)`

   This class interacts with the content providers to fetch album and content data.

2. **Repositories**

    - **Album Repository (`AlbumsRepo`):** Acts as an intermediary between `GalleryDataSource` and ViewModels.

4. **UI Components**

    - **Fragments:** Display the album list and content using RecyclerView.
    - **Adapters:** Handle the presentation of items in list and grid views using enums to switch between modes.

### Usage

- **Run the App:** Build and run the app on a physical device or emulator with external storage access.
- **View Albums:** The app will list all available albums from your device.
- **Switch Views:** Toggle between list and grid views using the options provided in the UI.
- **View Images and Play Videos:** Intent is added to view images and play videos if there is any gallery or media player available on device.



### Contact

For any questions or feedback, please contact [waleedkalyar48@gmail.com](mailto:your-waleedkalyar48@gmail.com) or [+971 52944 7229](tel:+971529447229).

