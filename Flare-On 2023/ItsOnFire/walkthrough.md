To analyze the file `ItsOnFire.apk` I used the Java decompiler JADX ([here](https://github.com/skylot/jadx)).

## Initial observations
Opening the APK in Android Studio reveals that it is a "space invaders" kind of game, you can shoot all the "malwares" and they will simply respawn, so nothing of much interest to see here.
Opening the APK in JADX we can immediately see how the code is obfuscated and from navigatin the files we notice that Firebase is being used by the application, probably to implement a C2 mechanism.
The first thing to do is opening the Android manifest, particularly interesting are the following facts:
- The APK requires has the permission to make phone calls.
- The intent filters reveal which classes are monitoring which intents, intent filters are in fact needed to handle and process broadcast intents.

![android manifest intents](https://github.com/PietroColaguori/ReverseEngineering/blob/main/Flare-On%202023/ItsOnFire/images/manifest_intents.png)

Shown in the screenshot above are the intents for `com.secure.itsonfire.MalwareInvadersActivity` and for `com.secure.itsonfire.MessageWorker`.
The first intent sets the activity `MalwareInvadersActivity` to be run as the app's entry point, probably loading the screen for the game.
The second intent sets the service `MessageWorker` to be executed whenever a Firebase `MESSAGING_EVENT` takes place, probably for C2 communication, and all the other actions ensure persistency of the sample.

It seems natural to start by analyzing the class `com.secure.itsonfire.MalwareInvadersActivity`, which upon creating starts an instance of the `MalwareInvadersView` class, which is used to manage the game mechanics, so we don't care about it.
The next is looking into `com.secure.itsonfire.MessageWorker`, particularly interesting is the function `onNewToken` which is used to register the victim's device on the threat actor's Firebase-powered infrastructure via the created FCM token, now the C2 and the sample can communicate!
The strings are obfuscated, but we can recover their values from the `resources.arsc` which is automatically reconstructed by JADX, in particular we can find them in `/res/values/strings.xml`.
The FCM token is concatenated with the string `c2` which contains a sample URL of a C2 that uses the HTTP parameter `token` to register the victim's device, calling the function `PostByWeb`.

![fcm token registration](https://github.com/PietroColaguori/ReverseEngineering/blob/main/Flare-On%202023/ItsOnFire/images/http_request_construction.png)

Let's take a deeper look into the XML file containing the strings, a lot of interesting things come to mind, especially the fact that several strings are names of days of the week, that there is a string asking the user for the best day of the week, and that there are various strings referring to cryprography, especially AES.

![xml file strings](https://github.com/PietroColaguori/ReverseEngineering/blob/main/Flare-On%202023/ItsOnFire/images/xml_strings.png)

I tried checking where the different day of the week strings are used, and the search brought me to class `c`, which implements a check on the data supplied by the intent and based on that decides which actions to perform. Basically, different days of the weeks means different actions performed by the sample, now we I just need to find out which one is the desired one to replicate.

![command handler class c](https://github.com/PietroColaguori/ReverseEngineering/blob/main/Flare-On%202023/ItsOnFire/images/class_c_different_actions.png)

Basically, the logic is the following:
- Monday: a number is called, can be recovered from the strings.
- Tuesday: an operation is performed on image `ps.png`.
- Wednesday: an operation is performed on image `iv.png`.
- Thursday: Google Maps is opened at a specific location.
- Friday: an X profile is opened.
- Saturday or Sunday: a YouTube video is loaded.

## Static analysis solution

Probably, the only two interesting scenarios are the ones where an image is loaded from disk and actions are performed on it, surely some cryptography operations that will reveal the flag.
The name IV sounds cryptography-related, so I go with trying to replicate the scenario in which `wednesday` is passed as argument.
This brings us to class `b`, whose original code can be found [here](https://github.com/PietroColaguori/ReverseEngineering/blob/main/Flare-On%202023/ItsOnFire/original_class_b.java).
Below is a description of what each function in the class does, to understand how the flag is obtained:
1. `d`: the two strings `https://flare-on.com/evilc2server/report_token/report_token.php?token=` and `wednesday` are manipulated to generate a new string `s://fldne`. This new string is used to compute a CRC32 checksum, which is appended to itself and then the slice `[0, 15]` of the resulting string is returned.
2. `c`: loads the `iv.png` file from resources using function `e`, the bytes that make up the file are decrypted using the crypto scheme `AES/CBC/PKCS5Padding` and the string returned by `d` as secret key. The resulting bytes are used to create a new file called `playerscore.png`. This file likely contains the flag of the challenge.

I created a Java program that does all of this to sovle the challenge statically, the bytes are loaded from the location of the PNG file as is recovered by JADX. The program's code can be found [here](https://github.com/PietroColaguori/ReverseEngineering/blob/main/Flare-On%202023/ItsOnFire/GetFlag.java).

![flag](https://github.com/PietroColaguori/ReverseEngineering/blob/main/Flare-On%202023/ItsOnFire/images/playerscore.png)


