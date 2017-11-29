Arriving at the 2nd TD, we realized we were sending empty messages, even if we had received and parsed correctly other messages. 
As a consequence, our internal peer table was up to date, but we were inconsistent to everyone else.

Trying to resolve the problem, we came to understand the need for debugging tools, as everyone else in the TD was sending futuristic messages (SYN and LIST) when we were not yet there.