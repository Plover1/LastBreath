Add-Type -AssemblyName System.Drawing
$root='C:\Users\Vlad\.codex\generated_images\01a14e41-fd25-7831-a996-501ab25ed225'
function Tile($name,$target){
 $src=[Drawing.Image]::FromFile((Join-Path $root $name));$dest=[Drawing.Bitmap]::new(32,32,[Drawing.Imaging.PixelFormat]::Format32bppArgb);$g=[Drawing.Graphics]::FromImage($dest);$g.InterpolationMode=[Drawing.Drawing2D.InterpolationMode]::NearestNeighbor;$g.PixelOffsetMode=[Drawing.Drawing2D.PixelOffsetMode]::Half;$g.CompositingMode=[Drawing.Drawing2D.CompositingMode]::SourceCopy;$g.DrawImage($src,[Drawing.Rectangle]::new(0,0,32,32),0,0,$src.Width,$src.Height,[Drawing.GraphicsUnit]::Pixel);New-Item -ItemType Directory -Force (Split-Path $target)|Out-Null;$dest.Save((Join-Path $pwd $target),[Drawing.Imaging.ImageFormat]::Png);$g.Dispose();$dest.Dispose();$src.Dispose()
}
Tile 'exec-a5d6c65f-8183-4c7a-b74d-5aeb66d9a171.png' 'src/main/resources/assets/lastbreath/textures/item/soul_devourer.png'
Tile 'exec-d644fd4c-0109-4d02-affb-2267dc6d653e.png' 'src/main/resources/assets/lastbreath/textures/item/soul_cloak.png'
Tile 'exec-daf36f11-c61c-4855-a583-72ffdb5ca8a1.png' 'src/main/resources/assets/lastbreath/textures/entity/equipment/humanoid/soul_cloak.png'
