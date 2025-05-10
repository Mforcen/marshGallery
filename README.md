# marshGallery

A webdav based photo gallery.
The motivation behind this app is to refurbish an off-the-self digital photo frame that has Android 
6 installed.
Webdav is used to load files from compatible cloud storages, such as Nextcloud.
It loads a random image each 10 minutes, showing it until a new image arrives.

There are other better applications such as [Les Pas](https://f-droid.org/packages/site.leos.apps.lespas/)
but they require newer versions of Android.

# Privacy policy

This application does not show ads, nor it collect any kind of data. Application configuration is 
stored in the shared preferences of the app, and they will be deleted along the application.
Downloaded photos are not kept in disk, so the application will be constantly pinging the server
storage.