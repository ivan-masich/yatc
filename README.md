To build this app you need:<br/>
1. Create class OAuthConnectionData in package com.masich.yatc, and put to it CONSUMER_KEY and CONSUMER_SECRET from your twitter app;<br/>
2. Then run this commands from root of app to compile apk:

    android update project --path ./
    ant release
    
    or if you got error "Failure [INSTALL_FAILED_ALREADY_EXISTS]" when use adb install
    ant debug