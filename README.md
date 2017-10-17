Port Knocker
=====

Port Knocker
-----
Port Knocker is an Android application that allows people (who are running a port knock daemon) to send TCP or UDP packets to the specified ports. Based off the other app PortKnocking (originally the only one in the Android market), but faster, and semi-more reliable.

Usage Instructions
-----
Upon initial installation, the application will have an empty hosts list, and will present a message saying "No Hosts, use Menu->Add to add some."

Adding a Host
-----
1. To add a host, first click the menu button on your phone (either labeled with "Menu" or a picture of an indented list)
2. Choose the only option, "Add Host", which will take you to a new screen labeled "Add/Edit Host"
3. Fill out the "Host Name" field (the timeout is preset, but can be changed; it adjusts how long it will wait for a response from the remote host, so it affects the speed with which the knocks are sent)
4. Optional: If you want ConnectBot (available in the Market, not made by me) to be launched upon successful knocking (ex: using PortKnocker to allow ssh access into remote host), then fill out the "Host Nickname" (which should be the same as your saved settings in ConnectBot to use that profile), "Username", and "Port Number" (if different then 22).
5. Click "Save", after which you will be taken to another screen that is labeled "Port list for host: ..."
6. Since this is a new host, the Port list is empty, and will display a message saying "No ports for this host, use Menu->Add to add some."
7. To add ports, click the Menu button, then choose "Add Port"
8. If you want the knock for this port to be directed to a different host (other than the one that is automatically placed in the "Host Name" field), enter the desired host/IP address in the "Host Name" field.
9. Fill out the "Port Number" field, and select the "Packet Type" to send (either TCP or UDP)
10. Click "Save"
11. Repeat steps 7-9 for each additional port (Ports will be knocked in the order they are listed)
12. Use the back button (arrow pointing backwards) to exit the "Ports List" screen.

Editing a Host
-----
1. To edit a host, click and hold the name of the host to be edited
2. Select "Edit Host" from the contextual menu
3. Make changes

*NOTE If you change the IP address here, it will only change the IP address that is used for connect bot; it will NOT change the IP address for each port, that must be done for each port individually via editing of the ports. This will most likely be fixed in a future version.

4. Click "Save" to save changes, or "Cancel" to cancel changes (using the back button will also cancel changes)

Editing Ports
-----
1. To edit a hosts ports directly, click and hold the name of the host to be edited
2. Select "Edit Port(s)" from the contextual menu
3. To add ports, click the "Menu" button on your device, then choose "Add Port" (look at steps 7-10 of "Adding a Host" for more information)
4. To edit a port, click and hold the port to be changed.  Choose "Edit Port" to change the port's settings, or choose "Delete Port" to remove the selected port from the list.

Sending Knocks to a Host
-----
To send the port knocks to the host, either click the hostname to start knocking
  -- or --
Click and hold the host's name to bring up a contextual menu, then choose "Send Knock"

Privacy Policy
-----
This application does not access, store, nor transmit user data. Only data entered into the application is stored within the application on this device, and is not stored outside of this device.
