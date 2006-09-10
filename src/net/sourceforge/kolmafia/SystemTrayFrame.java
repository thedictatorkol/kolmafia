package net.sourceforge.kolmafia;

import java.io.File;
import javax.swing.ImageIcon;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import com.jeans.trayicon.WindowsTrayIcon;
import com.jeans.trayicon.TrayIconPopup;
import com.jeans.trayicon.TrayIconPopupSimpleItem;

import net.java.dev.spellcast.utilities.JComponentUtilities;

public abstract class SystemTrayFrame implements KoLConstants
{
	private static WindowsTrayIcon icon = null;

	public static void updateToolTip()
	{
		if ( icon != null )
		{
			icon.setVisible( true );
			if ( KoLCharacter.getUsername().equals( "" ) )
				icon.setToolTipText( VERSION_NAME );
			else
				icon.setToolTipText( VERSION_NAME + ": " + KoLCharacter.getUsername() );
		}
	}

	public static void addTrayIcon()
	{
		// Now, make calls to SystemTrayIconManager in order
		// to make use of the system tray.

		if ( !StaticEntity.loadLibrary( "TrayIcon12.dll" ) )
		{
			KoLmafia.updateDisplay( "Failed to load tray icon." );
			return;
		}

		System.load( (new File( "images/TrayIcon12.dll" )).getAbsolutePath() );
		WindowsTrayIcon.initTrayIcon( "KoLmafia" );

		try
		{
			StaticEntity.loadLibrary( "TrayIcon12.gif" );
			ImageIcon image = JComponentUtilities.getImage( "", "TrayIcon12.gif" );

			icon = new WindowsTrayIcon( image.getImage(), 16, 16 );
			icon.addMouseListener( new SetVisibleListener() );

			TrayIconPopup popup = new TrayIconPopup();
			popup.addMenuItem( new EndSessionPopupItem() );

			icon.setPopup( popup );
			updateToolTip();
		}
		catch ( Exception e )
		{
			StaticEntity.printStackTrace( e );
			return;
		}
	}

	public static void removeTrayIcon()
	{	WindowsTrayIcon.cleanUp();
	}

	private static class SetVisibleListener extends MouseAdapter
	{
		public void mousePressed( MouseEvent e )
		{
			if ( e.getClickCount() != 2 )
				return;

			KoLFrame [] frames = new KoLFrame[ existingFrames.size() ];
			existingFrames.toArray( frames );

			String interfaceSetting = StaticEntity.getProperty( "initialDesktop" );

			for ( int i = 0; i < frames.length; ++i )
				if ( interfaceSetting.indexOf( frames[i].getFrameName() ) == -1 )
				{
					frames[i].setVisible( true );
					frames[i].setExtendedState( KoLFrame.NORMAL );
				}

			if ( KoLDesktop.instanceExists() )
			{
				KoLDesktop.getInstance().setVisible( true );
				KoLDesktop.getInstance().setExtendedState( KoLFrame.NORMAL );
			}
		}
	}

	private static class EndSessionPopupItem extends TrayIconPopupSimpleItem implements ActionListener
	{
		public EndSessionPopupItem()
		{
			super( "End Session" );
			addActionListener( this );
		}

		public void actionPerformed( ActionEvent e )
		{
			removeTrayIcon();
			System.exit(0);
		}
	}
}
