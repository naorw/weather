# Accessibility (v0.1)

Practical pass for GrapheneOS / Pixel. Not a WCAG certification.

## What was checked

- Contrast: graphite instrument palette (off-white on `#14171B`, teal/amber status). Status that is not LIVE stays amber.
- Touch targets: nav, Refresh, Recenter, layer chips, Cities GO, device-location, Settings save/remove use 48dp min height.
- TalkBack: nav destinations, Refresh, weather glyphs, 3-hour steps, day rows, wind compass, map Recenter and overlay chips, Cities search/device, Settings key actions.
- Font scaling: body/meta/heading already use `sp`. Extreme scale can wrap; screens remain scrollable. Nav labels may truncate at very large scale.
- Motion: no decorative animation. Map pan/zoom is user-driven.

## Known limits

- Map canvas itself is not a rich TalkBack map; overlay chips and Recenter are the labeled controls.
- Temperature hero uses a large `sp` size and will grow with system font.
