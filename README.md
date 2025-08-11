<div style="background-color:#2d2d2d; padding:20px; color:#fff;">
  <img src="Graphics/Banner.png" alt="Banner"
       width="600" height="142"
       style="display:block; margin:0 auto; width:600px; height:142px;">

<h2 id="toc_0" style="border-bottom:1px solid rgba(255,255,255,.2); padding-bottom:.3em;">Introduction</h2>
  <p>I needed an easy way to remove snapshots from my APFS volumes, so I wrote this simple little command line utility to handle that for me.</p>

<h2 id="toc_1" style="border-bottom:1px solid rgba(255,255,255,.2); padding-bottom:.3em;">Installation</h2>
  <p>The code was compiled into a MacOS Native Binary and does not need Java to run. So you have two options:</p>
<p>1) Download the zip file from the <a href="https://github.com/EasyG0ing1/SnapZap/releases/latest">latest release</a> page, unzip the file and put it in a folder in your path (such as ~/.local/bin)</p>
<p>2) Install with Homebrew</p>

<pre style="background:#2f3337; color:#e6edf3; padding:16px; border-radius:6px; overflow:auto; border:1px solid rgba(255,255,255,.12); margin:16px 0;">
brew update
brew tap EasyG0ing1/tools
brew install snapzap
  </pre>


<h2 id="toc_2" style="border-bottom:1px solid rgba(255,255,255,.2); padding-bottom:.3em;">Usage</h2>
  <p>Invoke the command with the path to the volume you want to manage. Then chose a menu option.</p>

  <pre style="background:#2f3337; color:#e6edf3; padding:16px; border-radius:6px; overflow:auto; border:1px solid rgba(255,255,255,.12); margin:16px 0;">
<code style="white-space:pre; font-family:ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono','Courier New', monospace;">snapzap -v /Volumes/MyVolume
snapzap -v MyVolume</code>
  </pre>

  <p>Gives you the menu</p>

  <pre style="background:#2f3337; color:#e6edf3; padding:16px; border-radius:6px; overflow:auto; border:1px solid rgba(255,255,255,.12); margin:16px 0;">
<code style="white-space:pre; font-family:ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono','Courier New', monospace;">There are 230 snapshots on volume /Volumes/MyVolume

1) List snapshots
2) Purge one snapshot
3) Purge ALL snapshots
Q) Quit

Choice:</code>
  </pre>

<h2 id="toc_3" style="border-bottom:1px solid rgba(255,255,255,.2); padding-bottom:.3em;">List</h2>
  <p>Listing the snapshots via the menu or command line</p>

  <pre style="background:#2f3337; color:#e6edf3; padding:16px; border-radius:6px; overflow:auto; border:1px solid rgba(255,255,255,.12); margin:16px 0;">
<code style="white-space:pre; font-family:ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono','Courier New', monospace;">snapzap -v MyVolume -l
snapzap -v MyVolume --list</code>
  </pre>

  <p>it will look like this</p>

  <pre style="background:#2f3337; color:#e6edf3; padding:16px; border-radius:6px; overflow:auto; border:1px solid rgba(255,255,255,.12); margin:16px 0;">
<code style="white-space:pre; font-family:ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono','Courier New', monospace;">Disk: disk5s1
UUID: 3195696A-9221-4750-B01F-A1D97D366799
Name: com.bombich.ccc.FCB93B5F-E887-4A3F-BC01-33042E093FB4.2022-12-09-060559
XID: 436280
Purgeable: YES

Disk: disk5s1
UUID: 0E6157DA-C576-4909-87DA-D72B4EC1EA3A
Name: com.bombich.ccc.safetynet.5D009E90-E2F3-4987-8DD7-41C50FB8661B.2022-12-09-074901
XID: 436309
Purgeable: YES

Disk: disk5s1
UUID: 779D3E02-C809-4904-BA72-E429A331CBD1
Name: com.bombich.ccc.5D009E90-E2F3-4987-8DD7-41C50FB8661B.2022-12-09-074901
XID: 436312
Purgeable: YES</code>
  </pre>

<h2 id="toc_4" style="border-bottom:1px solid rgba(255,255,255,.2); padding-bottom:.3em;">Purge One</h2>
  <p>Purging one snapshot (only from the menu) will give you a numbered list of the snapshots using their name. Then you type in the number of the snapshot you want to delete.</p>

  <pre style="background:#2f3337; color:#e6edf3; padding:16px; border-radius:6px; overflow:auto; border:1px solid rgba(255,255,255,.12); margin:16px 0;">
<code style="white-space:pre; font-family:ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono','Courier New', monospace;">1) com.bombich.ccc.safetynet.B8E64E79-48E3-4EF2-AB54-09406D30855D.2022-12-03-053852
2) com.bombich.ccc.safetynet.BDB5B896-168F-4193-A057-3C115DBDE46B.2022-12-03-062403
3) com.bombich.ccc.safetynet.4B0D0728-EFCD-4315-A563-0485EDD5AA95.2022-12-03-081235
4) com.bombich.ccc.safetynet.799DB644-51FA-4590-A862-C7B693D765CC.2022-12-03-121845
5) com.bombich.ccc.safetynet.89B36A99-48F5-48DA-985F-9200A97B5632.2022-12-03-133147
6) com.bombich.ccc.safetynet.D0642620-CE25-40E7-86DF-8B62D0FFEA91.2022-12-03-155648

0) Main Menu

Choice: </code>
  </pre>

<h2 id="toc_5" style="border-bottom:1px solid rgba(255,255,255,.2); padding-bottom:.3em;">Purge All</h2>
  <p>Purging all snapshots (from the menu or CLI)</p>

  <pre style="background:#2f3337; color:#e6edf3; padding:16px; border-radius:6px; overflow:auto; border:1px solid rgba(255,255,255,.12); margin:16px 0;">
<code style="white-space:pre; font-family:ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono','Courier New', monospace;">snapzap -v MyVolume -p
snapzap -v MyVolume --purgeAll</code>
  </pre>

  <p>will ask for confirmation first:</p>

  <pre style="background:#2f3337; color:#e6edf3; padding:16px; border-radius:6px; overflow:auto; border:1px solid rgba(255,255,255,.12); margin:16px 0;">
<code style="white-space:pre; font-family:ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono','Courier New', monospace;">WARNING: This will DELETE all 92 snapshots on volume: /Volumes/Storage

Are you sure you want to proceed (Y/N)? </code>
  </pre>

<h2 id="toc_6" style="border-bottom:1px solid rgba(255,255,255,.2); padding-bottom:.3em;">Help</h2>

  <pre style="background:#2f3337; color:#e6edf3; padding:16px; border-radius:6px; overflow:auto; border:1px solid rgba(255,255,255,.12); margin:16px 0;">
<code style="white-space:pre; font-family:ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono','Courier New', monospace;">snapzap -h
snapzap --help</code>
  </pre>

  <pre style="background:#2f3337; color:#e6edf3; padding:16px; border-radius:6px; overflow:auto; border:1px solid rgba(255,255,255,.12); margin:16px 0;">
<code style="white-space:pre; font-family:ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono','Courier New', monospace;">Usage: App [-hlpV] [-v=path]

SnapZap helps you clean up snapshots on APFS volumes in MacOS.

-h, --help          Show this help message and exit.
-l, --list          List Snapshots (ex: -v /Volumes/Name -l)
-p, --purgeAll      Purge ALL snapshots
-v, --volume=path   Volume (ex: -v /Volumes/Name OR -v Name)
-V, --version       Print version information and exit.

Minimal required argument is -v (lower case) which will then give you a menu of options.

Examples:
snapzap -v /Volumes/MyVolume --list (Just provides the snapshot list)
snapzap -v MyVolume                 (Shows a menu of options)
snapzap -v MyVolume --purgeAll      (purges all snapshots on the volume after you confirm)

Typing '/Volumes/' before the volume name is optional as long as the volume exists in /Volumes

So these will also work:
snapzap -v MyVolume
snapzap -v MyVolume --list
snapzap -v MyVolume --purgeAll

etc..
</code>
  </pre>
</div>
