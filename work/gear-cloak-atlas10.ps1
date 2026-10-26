Add-Type -AssemblyName System.Drawing
$root='C:\Users\Vlad\.codex\generated_images\01a14e41-fd25-7831-a996-501ab25ed225'
$cloth=[Drawing.Image]::FromFile((Join-Path $root 'exec-daf36f11-c61c-4855-a583-72ffdb5ca8a1.png'))
$panel=[Drawing.Image]::FromFile((Join-Path $root 'exec-02383a2f-54ca-41ed-9202-62f5c75be262.png'))
$atlas=[Drawing.Bitmap]::new(128,64,[Drawing.Imaging.PixelFormat]::Format32bppArgb)
$g=[Drawing.Graphics]::FromImage($atlas);$g.InterpolationMode=[Drawing.Drawing2D.InterpolationMode]::NearestNeighbor;$g.PixelOffsetMode=[Drawing.Drawing2D.PixelOffsetMode]::Half
foreach($x in @(0,32,64,96)){$g.DrawImage($cloth,[Drawing.Rectangle]::new($x,0,32,32),0,0,$cloth.Width,$cloth.Height,[Drawing.GraphicsUnit]::Pixel)}
foreach($x in @(66,88)){$g.DrawImage($panel,[Drawing.Rectangle]::new($x,2,20,40),0,0,$panel.Width,$panel.Height,[Drawing.GraphicsUnit]::Pixel)}
$atlas.Save((Join-Path $pwd 'src/main/resources/assets/lastbreath/textures/entity/equipment/humanoid/soul_cloak.png'),[Drawing.Imaging.ImageFormat]::Png)
$g.Dispose();$atlas.Dispose();$cloth.Dispose();$panel.Dispose()
