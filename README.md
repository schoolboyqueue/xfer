# xfer

This was a personal project I started as a result of AIM's p2p file sharing service going offline and there was no
other way I was aware of for easy p2p (no middle service) file sharing. I started with the idea of just having a 
no frills p2p sharing program. I am interested in information security so I set out to make it a secure p2p file
sharing program to learn about the various encryption libraries available through java and how to use them.

This is a rough beta that works and uses RSA/AES for the encryption scheme, its own messaging protocol and
a multi-threaded server. There are design choices that I made before starting my undergrad that I would change, including
the messaging protocol having an entire overhaul. 
